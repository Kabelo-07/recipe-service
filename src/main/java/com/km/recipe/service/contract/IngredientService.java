package com.km.recipe.service.contract;

import com.km.recipe.dto.IngredientDTO;

import java.util.Collection;
import java.util.UUID;

public interface IngredientService {

    void addIngredients(UUID recipeId, Collection<IngredientDTO> dtos);

    void updateIngredients(UUID recipeId, Collection<IngredientDTO> dtos);

    void deleteIngredients(UUID recipeId, Collection<UUID> ingredientIds);
}
