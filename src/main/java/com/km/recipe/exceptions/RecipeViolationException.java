package com.km.recipe.exceptions;

public class RecipeViolationException extends RuntimeException {

    public RecipeViolationException(String name) {
        super(String.format("Recipe with name: %s, already exists", name));
    }

}
