package com.km.recipe.service.contract;

import com.km.recipe.domain.*;
import com.km.recipe.domain.repository.RecipeRepository;
import com.km.recipe.dto.*;
import com.km.recipe.dto.page.RecipePage;
import com.km.recipe.exceptions.InvalidRequestException;
import com.km.recipe.exceptions.RecipeNotFoundException;
import com.km.recipe.exceptions.RecipeViolationException;
import com.km.recipe.util.RecipeSpecificationBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RecipeServiceTest {

    @Autowired
    private RecipeRepository repository;

    @Autowired
    private RecipeService recipeService;


    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void willCreateNewRecipe_whenValidInfoIsPassed() {
        //given
        CreateRecipeDTO recipeDTO = CreateRecipeDTO.builder()
                .categoryType(CategoryDTO.BEEF)
                .cookingTime(25)
                .preparationTime(10)
                .name("Beef Stew")
                .ingredients(List.of(IngredientDTO.builder()
                                .description("200g Beef Cubes")
                                .build(),
                        IngredientDTO.builder()
                                .description("2 large onions")
                                .build()))
                .instructions(List.of(InstructionDTO.builder()
                                .description("Mix the things and voila")
                                .step(2)
                                .build(),
                        InstructionDTO.builder()
                                .description("Dice the onions")
                                .step(1)
                                .build()))
                .servings(3)
                .build();

        //when
        RecipeDTO createdRecipe = recipeService.create(recipeDTO);

        //then
        assertThat(createdRecipe.getId()).isNotNull();
        assertThat(createdRecipe.getCookingTime()).isEqualTo(25);
        assertThat(createdRecipe.getPreparationTime()).isEqualTo(10);
        assertThat(createdRecipe.getName()).isEqualTo("Beef stew");
        assertThat(createdRecipe.getIngredients()).hasSize(2);
        assertThat(createdRecipe.getInstructions()).hasSize(2);
    }

    @Test
    void willThrowRecipeViolationException_whenRecipeWithSameNameExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>();
        instructions.add(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build());
        instructions.add(Instruction.builder()
                .description("Mix the onions and tomatoes")
                .step(2)
                .build());

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build()));

        //and
        createRecipe("Old Chicken Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //when
        CreateRecipeDTO recipeDTO = CreateRecipeDTO.builder()
                .name("Old CHICKEN Recipe")
                .build();

        //then
        assertThatThrownBy(() -> recipeService.create(recipeDTO))
                .isInstanceOf(RecipeViolationException.class)
                .hasMessage("Recipe with name: " + recipeDTO.getName() + ", already exists");
    }

    @Test
    void willUpdateRecipeDetails() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>();
        instructions.add(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build());
        instructions.add(Instruction.builder()
                .description("Mix the onions and tomatoes")
                .step(2)
                .build());

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build()));

        //and
        Recipe recipe = createRecipe("Test Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        assertThat(recipe.getName()).isEqualTo("Test recipe");
        assertThat(recipe.getServings()).isEqualTo(5);
        assertThat(recipe.getPreparationTime()).isEqualTo(2);
        assertThat(recipe.getCookingTime()).isEqualTo(10);
        assertThat(recipe.getCategoryType().name()).isEqualTo(CategoryType.CHICKEN.name());
        assertThat(recipe.getInstructions()).hasSize(2);
        assertThat(recipe.getIngredients()).hasSize(2);

        //when
        recipe = repository.findById(recipe.getId()).orElseThrow();
        UpdateRecipeDTO updateRecipeDTO = UpdateRecipeDTO.builder()
                .cookingTime(125)
                .preparationTime(8)
                .servings(10)
                .categoryType(CategoryDTO.BEEF)
                .name("Best beef ever")
                .build();

        RecipeDTO updatedRecipe = recipeService.update(recipe.getId(), updateRecipeDTO);

        //then
        assertThat(updatedRecipe.getName()).isEqualTo("Best beef ever");
        assertThat(updatedRecipe.getServings()).isEqualTo(10);
        assertThat(updatedRecipe.getPreparationTime()).isEqualTo(8);
        assertThat(updatedRecipe.getCookingTime()).isEqualTo(125);
        assertThat(updatedRecipe.getCategoryType().name()).isEqualTo(CategoryType.BEEF.name());
        assertThat(recipe.getInstructions()).hasSize(2);
        assertThat(recipe.getIngredients()).hasSize(2);
    }

    @Test
    void willThrowRecipeNotFoundException_whenUpdatingNonExistingRecipe() {
        //given
        UpdateRecipeDTO updateRecipeDTO = UpdateRecipeDTO.builder().build();

        //and
        UUID recipeId = UUID.randomUUID();

        //then
        assertThatThrownBy(() -> recipeService.update(recipeId, updateRecipeDTO))
                .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void willThrowRecipeViolationException_whenUpdatingRecipe_withAlreadyExistingRecipeName() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>();
        instructions.add(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build());
        instructions.add(Instruction.builder()
                .description("Mix the onions and tomatoes")
                .step(2)
                .build());

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build()));

        //and
        createRecipe("Classic Chicken", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        Recipe recipeTwo = createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //when
        UpdateRecipeDTO updateRecipeDTO = UpdateRecipeDTO.builder()
                .name("Classic Chicken")
                .build();

        //then
        UUID recipeTwoId = recipeTwo.getId();
        assertThatThrownBy(() -> recipeService.update(recipeTwoId, updateRecipeDTO))
                .isInstanceOf(RecipeViolationException.class)
                .hasMessage("Recipe with name: " + updateRecipeDTO.getName() + ", already exists");
    }

    @Test
    void willUpdateExistingIngredients_forExistingRecipe() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>();
        instructions.add(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build());

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build()));

        //and
        Recipe recipe = createRecipe("Test Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        assertThat(recipe.getIngredients()).hasSize(2);
        Set<Ingredient> recipeIngredients = recipe.getIngredients();
        Ingredient first = recipeIngredients.iterator().next();

        assertThat(first.getId()).isNotNull();
        assertThat(first.getDescription()).isEqualTo("5 Tomatoes");

        //when
        IngredientDTO ingredientDTO = IngredientDTO.builder()
                .id(first.getId())
                .description("10 Tomatoes")
                .build();

        RecipeDTO updatedRecipe = recipeService.updateIngredients(recipe.getId(), List.of(ingredientDTO));

        //then
        assertThat(updatedRecipe.getIngredients()).hasSize(2);
        IngredientDTO updatedIngredient = updatedRecipe.getIngredients().iterator().next();

        assertThat(updatedIngredient.getId()).isNotNull();
        assertThat(updatedIngredient.getDescription()).isEqualTo("10 Tomatoes");
    }

    @Test
    void willAddNewIngredient_toExistingRecipe() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(Set.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe recipe = createRecipe("Test Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        assertThat(recipe.getIngredients()).hasSize(1);
        Set<Ingredient> recipeIngredients = recipe.getIngredients();
        Ingredient first = recipeIngredients.iterator().next();
        assertThat(first.getId()).isNotNull();
        assertThat(first.getDescription()).isEqualTo("5 Tomatoes");

        //when
        IngredientDTO ingredientDTO = IngredientDTO.builder()
                .description("Cucumber")
                .build();

        RecipeDTO updatedRecipe = recipeService.addIngredients(recipe.getId(), List.of(ingredientDTO));

        //then
        assertThat(updatedRecipe.getIngredients()).hasSize(2);
        IngredientDTO firstIngredient = updatedRecipe.getIngredients().stream().findFirst().get();
        IngredientDTO lastIngredient = updatedRecipe.getIngredients().stream().reduce((dto, dto2) -> dto2).get();
        assertThat(firstIngredient.getId()).isNotNull();
        assertThat(firstIngredient.getDescription()).isEqualTo("5 tomatoes");
        assertThat(lastIngredient.getId()).isNotNull();
        assertThat(lastIngredient.getDescription()).isEqualTo("Cucumber");
    }

    @Test
    void willUpdateExistingInstructions_forExistingRecipe() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(Set.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe recipe = createRecipe("Test Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        assertThat(recipe.getInstructions()).hasSize(1);
        Set<Instruction> instructionSet = recipe.getInstructions();
        Instruction first = instructionSet.stream().findFirst().get();
        assertThat(first.getId()).isNotNull();
        assertThat(first.getDescription()).isEqualTo("Chop the tomatoes");
        assertThat(first.getDetailedDescription()).isBlank();
        assertThat(first.getStep()).isEqualTo(1);

        //when
        InstructionDTO instructionDTO = InstructionDTO.builder()
                .id(first.getId())
                .description("Dice the tomatoes")
                .detailedDescription("Dice the tomatoes evenly, you can grate them if required")
                .build();

        RecipeDTO updatedRecipe = recipeService.updateInstructions(recipe.getId(), List.of(instructionDTO));

        //then
        assertThat(updatedRecipe.getIngredients()).hasSize(1);
        InstructionDTO updated = updatedRecipe.getInstructions().stream().findFirst().get();

        assertThat(updated.getId()).isNotNull();
        assertThat(updated.getDescription()).isEqualTo("Dice the tomatoes");
        assertThat(updated.getDetailedDescription()).isNotBlank();
        assertThat(updated.getStep()).isEqualTo(1);
    }

    @Test
    void willAddNewInstruction_toExistingRecipe() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(Set.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe recipe = createRecipe("Test Recipe", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        assertThat(recipe.getInstructions()).hasSize(1);
        Set<Instruction> instructionSet = recipe.getInstructions();
        Instruction first = instructionSet.stream().findFirst().get();
        assertThat(first.getId()).isNotNull();
        assertThat(first.getDescription()).isEqualTo("Chop the tomatoes");

        //when
        InstructionDTO instructionDTO = InstructionDTO.builder()
                .description("Peel the onions and dice them")
                .step(2)
                .build();

        RecipeDTO updatedRecipe = recipeService.addInstructions(recipe.getId(), List.of(instructionDTO));

        //then
        assertThat(updatedRecipe.getInstructions()).hasSize(2);
        InstructionDTO firstInstruction = updatedRecipe.getInstructions().stream().findFirst().get();
        InstructionDTO lastInstruction = updatedRecipe.getInstructions().stream().reduce((dto, dto2) -> dto2).get();
        assertThat(firstInstruction.getId()).isNotNull();
        assertThat(firstInstruction.getDescription()).isEqualTo("Chop the tomatoes");
        assertThat(firstInstruction.getStep()).isEqualTo(1);
        assertThat(lastInstruction.getId()).isNotNull();
        assertThat(lastInstruction.getDescription()).isEqualTo("Peel the onions and dice them");
        assertThat(lastInstruction.getStep()).isEqualTo(2);
    }

    @Test
    void willFindRecipeById_whenRecipeExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>();
        instructions.add(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build());
        instructions.add(Instruction.builder()
                .description("Mix the onions and tomatoes")
                .step(2)
                .build());
        instructions.add(Instruction.builder()
                .description("Chop the peppers evenly")
                .step(3)
                .build());

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build(),
                Ingredient.builder()
                        .description("1 green pepper")
                        .build(),
                Ingredient.builder()
                        .description("1 red pepper")
                        .build(),
                Ingredient.builder()
                        .description("Cucumber, finely chopped")
                        .build()));

        //and
        Recipe recipe = createRecipe("Some vegetarian Meal", 2, 10, 25,
                CategoryType.VEGETARIAN, ingredients, instructions);

        //when
        RecipeDTO dto = recipeService.findById(recipe.getId());

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("Some vegetarian meal");
        assertThat(dto.getServings()).isEqualTo(2);
        assertThat(dto.getPreparationTime()).isEqualTo(10);
        assertThat(dto.getCookingTime()).isEqualTo(25);
        assertThat(dto.getCategoryType()).isEqualTo(CategoryDTO.VEGETARIAN);
        assertThat(dto.getInstructions()).hasSize(3);
        assertThat(dto.getIngredients()).hasSize(5);
    }

    @Test
    void willThrowRecipeNotFoundException_whenRecipeDoesNotExist() {
        //given
        UUID recipeId = UUID.randomUUID();

        //then
        assertThatThrownBy(() -> recipeService.findById(recipeId))
                .isInstanceOf(RecipeNotFoundException.class)
                .hasMessage("Recipe with id: " + recipeId + ", not found");
    }

    @Test
    void willReturnAllRecipes() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        createRecipe("Classic Chicken", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test vinegar")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //when
        Specification<Recipe> specification = new RecipeSpecificationBuilder().build();
        RecipePage recipePage = recipeService.findAll(specification, PageRequest.of(0, 5));

        //then
        assertThat(recipePage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void willReturnRecipesWithExcludedIngredients() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe classicChicken = createRecipe("Classic Chicken", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test vinegar")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //and
        RecipePage recipePage = recipeService.findAll(new RecipeSpecificationBuilder().build(),
                PageRequest.of(0, 5));
        assertThat(recipePage.getTotalElements()).isEqualTo(2);

        //when
        Specification<Recipe> specification = new RecipeSpecificationBuilder()
                .withExcludedIngredients(List.of("VINEGAR"))
                .build();
        recipePage = recipeService.findAll(specification, PageRequest.of(0, 5));

        //then
        assertThat(recipePage.getTotalElements()).isEqualTo(1);

        //and
        RecipeDTO recipeDTO = recipePage.getContent().iterator().next();
        assertThat(recipeDTO.getId()).isEqualTo(classicChicken.getId());
        assertThat(recipeDTO.getName()).isEqualTo("Classic chicken");
    }

    @Test
    void willReturnRecipesWithIncludedIngredients() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        createRecipe("Classic Chicken", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        Recipe classicBeef = createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test vinegar")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //when
        Specification<Recipe> specification = new RecipeSpecificationBuilder()
                .withIncludedIngredients(List.of("VINEGAR"))
                .build();
        RecipePage recipePage = recipeService.findAll(specification, PageRequest.of(0, 5));

        //then
        assertThat(recipePage.getTotalElements()).isEqualTo(1);

        //and
        RecipeDTO recipeDTO = recipePage.getContent().iterator().next();
        assertThat(recipeDTO.getId()).isEqualTo(classicBeef.getId());
        assertThat(recipeDTO.getName()).isEqualTo("Classic beef");
    }

    @Test
    void willReturnEmptyRecipesPage_whenVegetarianCategoryIsPassed_whenNonVegetarianRecipesExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        createRecipe("Classic Chicken", 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);

        //and
        createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test vinegar")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //when
        Specification<Recipe> specification = new RecipeSpecificationBuilder()
                .withCategory(CategoryDTO.VEGETARIAN)
                .build();
        RecipePage recipePage = recipeService.findAll(specification, PageRequest.of(0, 5));

        //then
        assertThat(recipePage.getTotalElements()).isZero();
        assertThat(recipePage.getContent()).isEmpty();
    }

    @Test
    void willReturnVegetarianRecipes_whenVegetarianCategoryIsPassed_whenVegetarianRecipesExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe vegetarian = createRecipe("Some vegetarian", 5, 2, 10,
                CategoryType.VEGETARIAN, ingredients, instructions);

        //and
        createRecipe("Classic Beef", 2, 25, 80,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Test vinegar")
                        .build())),
                new TreeSet<>(Set.of(Instruction.builder()
                        .step(1)
                        .description("Test")
                        .build())));

        //when
        Specification<Recipe> specification = new RecipeSpecificationBuilder()
                .withCategory(CategoryDTO.VEGETARIAN)
                .build();
        RecipePage recipePage = recipeService.findAll(specification, PageRequest.of(0, 5));

        //then
        assertThat(recipePage.getTotalElements()).isEqualTo(1);
        RecipeDTO recipeDTO = recipePage.getContent().iterator().next();
        assertThat(recipeDTO.getCategoryType()).isEqualTo(CategoryDTO.VEGETARIAN);
        assertThat(recipeDTO.getId()).isEqualTo(vegetarian.getId());
        assertThat(recipeDTO.getName()).isEqualTo(vegetarian.getName());
        assertThat(recipeDTO.getServings()).isEqualTo(vegetarian.getServings());
        assertThat(recipeDTO.getPreparationTime()).isEqualTo(vegetarian.getPreparationTime());
        assertThat(recipeDTO.getCookingTime()).isEqualTo(vegetarian.getCookingTime());
        assertThat(recipeDTO.getInstructions()).isNotEmpty();
        assertThat(recipeDTO.getIngredients()).isNotEmpty();
    }

    @Test
    void testWillDeleteRecipe_ifExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe recipe = createRecipe("Some vegetarian", 5, 2, 10,
                CategoryType.VEGETARIAN, ingredients, instructions);

        //and
        assertThat(repository.findById(recipe.getId())).isPresent();

        //when
        recipeService.delete(recipe.getId());

        //then
        assertThat(repository.findById(recipe.getId())).isNotPresent();
    }

    @Test
    void testWillDeleteRecipeInstruction_ifExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build(),
                Instruction.builder()
                        .description("Peel the onion")
                        .step(2)
                        .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                .description("5 Tomatoes")
                .build()));

        //and
        Recipe recipe = createRecipe("Some vegetarian", 5, 2, 10,
                CategoryType.VEGETARIAN, ingredients, instructions);

        //and
        assertThat(recipe.getInstructions()).hasSize(2);
        Optional<Instruction> instruction = recipe.getInstructions().stream().findFirst();
        assertThat(instruction).isPresent();

        //when
        recipeService.deleteInstructions(recipe.getId(), List.of(instruction.get().getId()));

        //then
        recipe = repository.findById(recipe.getId()).get();
        assertThat(recipe.getInstructions()).hasSize(1);
        assertThat(recipe.getInstructions()).doesNotContain(instruction.get());
    }

    @Test
    void testWillDeleteRecipeIngredient_ifExists() {
        //given
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                .description("Chop the tomatoes")
                .step(1)
                .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Baby potatoes")
                        .build()));

        //and
        Recipe recipe = createRecipe("Some vegetarian", 5, 2, 10,
                CategoryType.VEGETARIAN, ingredients, instructions);

        //and
        assertThat(recipe.getIngredients()).hasSize(2);
        Optional<Ingredient> ingredient = recipe.getIngredients().stream().findFirst();
        assertThat(ingredient).isPresent();

        //when
        recipeService.deleteIngredients(recipe.getId(), List.of(ingredient.get().getId()));

        //then
        recipe = repository.findById(recipe.getId()).get();
        assertThat(recipe.getIngredients()).hasSize(1);
        assertThat(recipe.getIngredients()).doesNotContain(ingredient.get());
    }

    @Test
    void testWillThrowException_whenDeleteRecipeNotExisting() {
        //given
        UUID recipeId = UUID.randomUUID();

        //then
        assertThatThrownBy(() -> recipeService.delete(recipeId))
                .isInstanceOf(InvalidRequestException.class);
    }

    private Recipe createRecipe(String name, int servings, int prepTime, int cookingTime,
                                CategoryType categoryType, SortedSet<Ingredient> ingredients, SortedSet<Instruction> instructions) {
        return repository.save(Recipe.builder()
                .servings(servings)
                .name(name)
                .preparationTime(prepTime)
                .cookingTime(cookingTime)
                .categoryType(categoryType)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .ingredients(ingredients)
                .instructions(instructions)
                .build());
    }
}