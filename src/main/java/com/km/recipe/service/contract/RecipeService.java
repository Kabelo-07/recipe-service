package com.km.recipe.service.contract;

import com.km.recipe.domain.Recipe;
import com.km.recipe.dto.*;
import com.km.recipe.dto.page.RecipePage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface RecipeService {

    RecipeDTO create(CreateRecipeDTO dto);

    RecipeDTO update(UUID recipieId, UpdateRecipeDTO dto);

    RecipeDTO findById(UUID recipeId);

    RecipePage findAll(Specification<Recipe> specification, Pageable pageable);

    void delete(UUID recipeId);

    void deleteInstructions(UUID recipeId, List<UUID> instructionIds);

    void deleteIngredients(UUID recipeId, List<UUID> ingredientIds);

    RecipeDTO updateIngredients(UUID recipeId, List<IngredientDTO> ingredients);

    RecipeDTO addIngredients(UUID recipeId, List<IngredientDTO> ingredients);

    RecipeDTO updateInstructions(UUID recipeId, List<InstructionDTO> instructions);

    RecipeDTO addInstructions(UUID recipeId, List<InstructionDTO> instructions);
}
