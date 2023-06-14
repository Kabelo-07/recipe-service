package com.km.recipe.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.km.recipe.WebIntegrationTest;
import com.km.recipe.domain.CategoryType;
import com.km.recipe.domain.Ingredient;
import com.km.recipe.domain.Instruction;
import com.km.recipe.domain.Recipe;
import com.km.recipe.domain.repository.RecipeRepository;
import com.km.recipe.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebIntegrationTest
class RecipeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RecipeRepository repository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void willReturnCreatedHttpStatus_whenAddingRecipeWithValidInfo() throws Exception {
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
        ResultActions actions = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(recipeDTO)));

        //then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Beef stew"))
                .andExpect(jsonPath("$.servings").value(recipeDTO.getServings()))
                .andExpect(jsonPath("$.cooking_time").value(recipeDTO.getCookingTime()))
                .andExpect(jsonPath("$.preparation_time").value(recipeDTO.getPreparationTime()))
                .andExpect(jsonPath("$.category").value(recipeDTO.getCategoryType().name()))
                .andExpect(jsonPath("$.ingredients.*", hasSize(2)))
                .andExpect(jsonPath("$.ingredients[0].id").isNotEmpty())
                .andExpect(jsonPath("$.ingredients[0].description").value("2 large onions"))
                .andExpect(jsonPath("$.ingredients[1].id").isNotEmpty())
                .andExpect(jsonPath("$.ingredients[1].description").value("200g Beef Cubes"))
                .andExpect(jsonPath("$.instructions.*", hasSize(2)))
                .andExpect(jsonPath("$.instructions[0].id").isNotEmpty())
                .andExpect(jsonPath("$.instructions[0].step").value(1))
                .andExpect(jsonPath("$.instructions[0].description").value("Dice the onions"))
                .andExpect(jsonPath("$.instructions[1].id").isNotEmpty())
                .andExpect(jsonPath("$.instructions[1].step").value(2))
                .andExpect(jsonPath("$.instructions[1].description").value("Mix the things and voila"));
    }

    @Test
    void willReturnBadRequestHttpStatus_whenRequiredRecipeInfoNotProvided() throws Exception {
        //given
        CreateRecipeDTO recipeDTO = CreateRecipeDTO.builder()
                .categoryType(CategoryDTO.BEEF)
                .cookingTime(25)
                .preparationTime(10)
                .servings(3)
                .build();

        //when
        ResultActions actions = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(recipeDTO)));

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request sent"))
                .andExpect(jsonPath("$.errors.*").isNotEmpty());
    }

    @Test
    void willReturnConflictHttpStatus_whenRecipeNameAlreadyExists() throws Exception {
        //given
        Recipe recipe = mockRecipe("My Recipe");
        recipe = repository.save(recipe);

        //and
        CreateRecipeDTO recipeDTO = CreateRecipeDTO.builder()
                .categoryType(CategoryDTO.BEEF)
                .cookingTime(25)
                .preparationTime(10)
                .name("My Recipe")
                .ingredients(List.of(IngredientDTO.builder()
                        .description(UUID.randomUUID().toString())
                        .build()))
                .instructions(List.of(InstructionDTO.builder()
                        .step(1)
                        .description(UUID.randomUUID().toString())
                        .build()))
                .servings(3)
                .build();

        //when
        ResultActions actions = mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(recipeDTO)));

        //then
        actions.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Recipe with name: My Recipe, already exists"));
    }

    @Test
    void testWillReturnOk_whenExistingRecipe_isUpdated() throws Exception {
        //given
        Recipe recipe = mockRecipe("My Recipe");
        recipe = repository.save(recipe);

        //when
        UpdateRecipeDTO recipeDTO = UpdateRecipeDTO.builder()
                .name("My famous recipe")
                .servings(100)
                .cookingTime(200)
                .preparationTime(200)
                .build();

        ResultActions actions = mockMvc.perform(put("/recipes/{recipeId}", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(recipeDTO)));

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId().toString()))
                .andExpect(jsonPath("$.name").value("My famous recipe"))
                .andExpect(jsonPath("$.servings").value(100))
                .andExpect(jsonPath("$.cooking_time").value(200))
                .andExpect(jsonPath("$.preparation_time").value(200))
                .andExpect(jsonPath("$.category").value(recipe.getCategoryType().name()));
    }

    @Test
    void willReturnRecipe_whenExistsWithGivenId() throws Exception {
        //given
        Recipe recipe = mockRecipe("My Recipe");
        recipe = repository.save(recipe);

        //when
        ResultActions actions = mockMvc.perform(get("/recipes/{recipeId}", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipe.getId().toString()))
                .andExpect(jsonPath("$.name").value(recipe.getName()))
                .andExpect(jsonPath("$.servings").value(recipe.getServings()))
                .andExpect(jsonPath("$.cooking_time").value(recipe.getCookingTime()))
                .andExpect(jsonPath("$.preparation_time").value(recipe.getPreparationTime()))
                .andExpect(jsonPath("$.category").value(recipe.getCategoryType().name()))
                .andExpect(jsonPath("$.ingredients.*", hasSize(2)))
                .andExpect(jsonPath("$.ingredients[0].id").isNotEmpty())
                .andExpect(jsonPath("$.ingredients[0].description").value("5 tomatoes"))
                .andExpect(jsonPath("$.ingredients[1].id").isNotEmpty())
                .andExpect(jsonPath("$.ingredients[1].description").value("Macaroni pasta"))
                .andExpect(jsonPath("$.instructions.*", hasSize(2)))
                .andExpect(jsonPath("$.instructions[0].id").isNotEmpty())
                .andExpect(jsonPath("$.instructions[0].step").value(1))
                .andExpect(jsonPath("$.instructions[0].description").value("Chop the tomatoes"))
                .andExpect(jsonPath("$.instructions[1].id").isNotEmpty())
                .andExpect(jsonPath("$.instructions[1].step").value(2))
                .andExpect(jsonPath("$.instructions[1].description").value("Mix the onions and tomatoes"));
    }

    @Test
    void willReturnNotFound_whenRecipeWithGivenId_isNotFound() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        //when
        ResultActions actions = mockMvc.perform(get("/recipes/{recipeId}", id)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Recipe with id: " + id + ", not found"));
    }

    @Test
    void testWillReturnNoContent_whenRecipeIsDeleted() throws Exception {
        //given
        Recipe recipe = mockRecipe("My Recipe");
        recipe = repository.save(recipe);

        //when
        mockMvc.perform(get("/recipes/{recipeId}", recipe.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //and
        ResultActions actions = mockMvc.perform(delete("/recipes/{recipeId}", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isNoContent());
    }

    @Test
    void testDeleteNonExistRecipe_willReturn409() throws Exception {
        //given
        UUID recipeId = UUID.randomUUID();

        //when
        ResultActions resultActions = mockMvc.perform(delete("/recipes/{recipeId}", recipeId)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Cannot remove recipe with id: " + recipeId));
    }

    @Test
    void willReturnOk_whenFetchingVegetarian_Recipes() throws Exception {
        //given
        createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("5 Tomatoes")
                                .build(),
                        Ingredient.builder()
                                .description("Macaroni pasta")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        createRecipe("Beef", 2, 10, 20,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("5 Tomatoes")
                                .build(),
                        Ingredient.builder()
                                .description("Macaroni pasta")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        //when
        ResultActions resultActions = mockMvc.perform(get("/recipes")
                .contentType(MediaType.APPLICATION_JSON));

        //and
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));


        //when filtering by Vegetarian mealCategory
        resultActions = mockMvc.perform(get("/recipes")
                .queryParam("mealCategory", CategoryDTO.VEGETARIAN.name())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Veggie"));


    }

    @Test
    void willReturnEmptyPage_whenFetchingNonExisting_ChickenRecipes() throws Exception {
        //given
        createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("5 Tomatoes")
                                .build(),
                        Ingredient.builder()
                                .description("Macaroni pasta")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        createRecipe("Beef", 2, 10, 20,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("5 Tomatoes")
                                .build(),
                        Ingredient.builder()
                                .description("Macaroni pasta")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        //when
        ResultActions resultActions = mockMvc.perform(get("/recipes")
                .contentType(MediaType.APPLICATION_JSON));

        //and
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));


        //when filtering by Chicken mealCategory
        resultActions = mockMvc.perform(get("/recipes")
                .queryParam("mealCategory", CategoryDTO.CHICKEN.name())
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.empty").value(true));

    }

    @Test
    void willReturnOk_whenFetchingRecipes_servingTwoPeople_andSaltIngredient() throws Exception {
        //given
        createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Cucumber")
                                .build(),
                        Ingredient.builder()
                                .description("Onion")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        createRecipe("Beef", 4, 10, 20,
                CategoryType.BEEF,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Something salty")
                                .build(),
                        Ingredient.builder()
                                .description("Tomatoes")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build()))
        );

        //when
        ResultActions resultActions = mockMvc.perform(get("/recipes")
                .contentType(MediaType.APPLICATION_JSON));

        //and
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));


        //when
        resultActions = mockMvc.perform(get("/recipes")
                .queryParam("servings", String.valueOf(4))
                .queryParam("withIngredients", "Salt")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].ingredients.*").isNotEmpty())
                .andExpect(jsonPath("$.content[0].ingredients[0].description").value("Something salty"));
    }

    @Test
    void willReturnOk_whenFetchingRecipes_withoutSaltIngredient_andCookedWithOven() throws Exception {
        //given
        createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Something very salty")
                        .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Put inside oven at 180 for 20mins")
                        .step(1)
                        .build()))
        );

        createRecipe("Chicken", 8, 10, 20,
                CategoryType.CHICKEN,
                new TreeSet<>(Set.of(Ingredient.builder()
                        .description("Tomatoes")
                        .build())),
                new TreeSet<>(List.of(Instruction.builder()
                                .description("Chop the tomatoes")
                                .step(1)
                                .build(),
                        Instruction.builder()
                                .description("Put inside oven at 180 for 20mins")
                                .step(2)
                                .build()))
        );

        //and
        ResultActions actions = mockMvc.perform(get("/recipes")
                .contentType(MediaType.APPLICATION_JSON));
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));

        //when
        actions = mockMvc.perform(get("/recipes")
                .queryParam("instructions", "Oven")
                .queryParam("excludeIngredients", "SALT")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void willReturnOk_whenDeletingIngredientFromRecipe() throws Exception {
        //given
        Recipe recipe = createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Something very salty")
                                .build(),
                        Ingredient.builder()
                                .description("Another one")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Put inside oven at 180 for 20mins")
                        .step(1)
                        .build()))
        );

        //and
        assertThat(recipe.getIngredients()).hasSize(2);
        Ingredient ingredient = recipe.getIngredients().stream().findFirst().get();

        //when
        ResultActions actions = mockMvc.perform(delete("/recipes/{recipeId}/ingredients", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(List.of(ingredient.getId()))));

        //then
        actions.andExpect(status().isNoContent());

        //and
        recipe = repository.findById(recipe.getId()).get();
        assertThat(recipe.getIngredients()).doesNotContain(ingredient);
    }

    @Test
    void willReturnOk_whenDeletingInstructionFromRecipe() throws Exception {
        //given
        Recipe recipe = createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Something very salty")
                                .build(),
                        Ingredient.builder()
                                .description("Another one")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                                .description("Put inside oven at 180 for 20mins")
                                .step(1)
                                .build(),
                        Instruction.builder()
                                .description("And this")
                                .step(2)
                                .build()))
        );

        //and
        assertThat(recipe.getInstructions()).hasSize(2);
        Instruction instruction = recipe.getInstructions().stream().findFirst().get();

        //when
        ResultActions actions = mockMvc.perform(delete("/recipes/{recipeId}/instructions", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(List.of(instruction.getId()))));

        //then
        actions.andExpect(status().isNoContent());

        //and
        recipe = repository.findById(recipe.getId()).get();
        assertThat(recipe.getInstructions()).doesNotContain(instruction);
    }

    @Test
    void willReturnOk_whenAddingInstruction_toRecipe() throws Exception {
        //given
        Recipe recipe = createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Something very salty")
                                .build(),
                        Ingredient.builder()
                                .description("Another one")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Put inside oven at 180 for 20mins")
                        .step(1)
                        .build())));

        //and
        assertThat(recipe.getInstructions()).hasSize(1);

        //when
        ResultActions actions = mockMvc.perform(post("/recipes/{recipeId}/instructions", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(List.of(
                        InstructionDTO.builder()
                                .step(2)
                                .description("A new one")
                                .build()))
                )
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.instructions.*", hasSize(2)));
    }

    @Test
    void willReturnOk_whenAddingIngredient_toRecipe() throws Exception {
        //given
        Recipe recipe = createRecipe("Veggie", 2, 10, 20,
                CategoryType.VEGETARIAN,
                new TreeSet<>(Set.of(Ingredient.builder()
                                .description("Something very salty")
                                .build(),
                        Ingredient.builder()
                                .description("Another one")
                                .build())),
                new TreeSet<>(List.of(Instruction.builder()
                        .description("Put inside oven at 180 for 20mins")
                        .step(1)
                        .build())));

        //and
        assertThat(recipe.getIngredients()).hasSize(2);

        //when
        ResultActions actions = mockMvc.perform(post("/recipes/{recipeId}/ingredients", recipe.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(List.of(
                        IngredientDTO.builder()
                                .description("A new one")
                                .build()))
                )
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients.*", hasSize(3)));
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

    public Recipe mockRecipe(String name) {
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(Instruction.builder()
                        .description("Chop the tomatoes")
                        .step(1)
                        .build(),
                Instruction.builder()
                        .description("Mix the onions and tomatoes")
                        .step(2)
                        .build()));

        SortedSet<Ingredient> ingredients = new TreeSet<>(Set.of(Ingredient.builder()
                        .description("5 Tomatoes")
                        .build(),
                Ingredient.builder()
                        .description("Macaroni pasta")
                        .build()));

        return createRecipe(name, 5, 2, 10,
                CategoryType.CHICKEN, ingredients, instructions);
    }

}