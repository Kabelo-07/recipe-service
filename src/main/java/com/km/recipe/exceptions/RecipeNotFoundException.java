package com.km.recipe.exceptions;

import java.util.UUID;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(UUID recipeId) {
        super(String.format("Recipe with id: %s, not found", recipeId));
    }
}
