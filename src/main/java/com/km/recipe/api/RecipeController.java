package com.km.recipe.api;

import com.km.recipe.domain.Recipe;
import com.km.recipe.dto.*;
import com.km.recipe.dto.page.RecipePage;
import com.km.recipe.service.contract.RecipeService;
import com.km.recipe.util.RecipeSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<RecipeDTO> addRecipe(@Valid @RequestBody CreateRecipeDTO dto) {
        RecipeDTO recipeDTO = recipeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeDTO);
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable("recipeId") UUID recipeId,
                                                  @RequestBody @Valid UpdateRecipeDTO dto) {
        RecipeDTO recipeDTO = recipeService.update(recipeId, dto);
        return ResponseEntity.ok(recipeDTO);
    }

    @PutMapping("/{recipeId}/ingredients")
    public ResponseEntity<RecipeDTO> updateIngredients(@PathVariable("recipeId") UUID recipeId,
                                                       @RequestBody @Valid List<IngredientDTO> ingredients) {
        RecipeDTO recipeDTO = recipeService.updateIngredients(recipeId, ingredients);
        return ResponseEntity.ok(recipeDTO);
    }

    @PutMapping("/{recipeId}/instructions")
    public ResponseEntity<RecipeDTO> updateInstructions(@PathVariable("recipeId") UUID recipeId,
                                                        @RequestBody @Valid List<InstructionDTO> instructions) {
        RecipeDTO recipeDTO = recipeService.updateInstructions(recipeId, instructions);
        return ResponseEntity.ok(recipeDTO);
    }

    @PostMapping("/{recipeId}/ingredients")
    public ResponseEntity<RecipeDTO> addIngredients(@PathVariable("recipeId") UUID recipeId,
                                                       @RequestBody @Valid List<IngredientDTO> ingredients) {
        RecipeDTO recipeDTO = recipeService.addIngredients(recipeId, ingredients);
        return ResponseEntity.ok(recipeDTO);
    }

    @PostMapping("/{recipeId}/instructions")
    public ResponseEntity<RecipeDTO> addInstructions(@PathVariable("recipeId") UUID recipeId,
                                                        @RequestBody @Valid List<InstructionDTO> instructions) {
        RecipeDTO recipeDTO = recipeService.addInstructions(recipeId, instructions);
        return ResponseEntity.ok(recipeDTO);
    }


    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDTO> findRecipeById(@PathVariable("recipeId") UUID recipeId) {
        RecipeDTO recipeDTO = recipeService.findById(recipeId);
        return ResponseEntity.ok(recipeDTO);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<RecipeDTO> deleteRecipe(@PathVariable("recipeId") UUID recipeId) {
        recipeService.delete(recipeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{recipeId}/ingredients")
    public ResponseEntity<RecipeDTO> deleteIngredients(@PathVariable("recipeId") UUID recipeId,
                                                       @RequestBody List<UUID> ingredientIds) {
        recipeService.deleteIngredients(recipeId, ingredientIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{recipeId}/instructions")
    public ResponseEntity<RecipeDTO> deleteInstructions(@PathVariable("recipeId") UUID recipeId,
                                                        @RequestBody List<UUID> instructionIds) {
        recipeService.deleteInstructions(recipeId, instructionIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<RecipePage> findRecipes(@RequestParam(value = "recipeId", required = false) UUID recipeId,
                                                  @RequestParam(value = "servings", required = false) Integer numberOfServings,
                                                  @RequestParam(value = "withIngredients", required = false) List<String> withIngredients,
                                                  @RequestParam(value = "excludeIngredients", required = false) List<String> excludeIngredients,
                                                  @RequestParam(value = "instructions", required = false) List<String> instructions,
                                                  @RequestParam(value = "mealCategory", required = false) CategoryDTO categoryDTO,
                                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNo,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "100") int pageSize) {

        Specification<Recipe> specification = new RecipeSpecificationBuilder()
                .withRecipeId(recipeId)
                .withServings(numberOfServings)
                .withExcludedIngredients(excludeIngredients)
                .withIncludedIngredients(withIngredients)
                .withInstructions(instructions)
                .withCategory(categoryDTO)
                .build();
        RecipePage recipePage = recipeService.findAll(specification, PageRequest.of(pageNo, pageSize));
        return ResponseEntity.ok(recipePage);
    }
}
