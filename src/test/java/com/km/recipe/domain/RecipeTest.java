package com.km.recipe.domain;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeTest {

    @Test
    void testWillCreateRecipe_withoutDuplicateIngredients() {
        //given ingredients - with duplicate ingredients of `2 Tomatoes, diced`
        SortedSet<Ingredient> ingredients = new TreeSet<>(List.of(ingredient("2 Tomatoes, diced"),
                ingredient("2 Tomatoes, diced"),
                ingredient("2tbsp of Sea Salt (or use any salt you have)"),
                ingredient("2tbsp of butter"),
                ingredient("2 Large onions"),
                ingredient("30g of Mozzarella cheese (use cheddar if you like)"),
                ingredient("500g of macaroni pasta")));

        //and instructions
        SortedSet<Instruction> instructions = new TreeSet<>(List.of(
                Instruction.builder()
                        .description("Ready to be served")
                        .step(4)
                        .build(),
                Instruction.builder()
                        .description("Chop all the vegetables and boil the macaroni")
                        .step(1)
                        .build(),
                Instruction.builder()
                        .description("Cook veggies in spices and mix with boiled macaroni")
                        .step(3)
                        .build(),
                Instruction.builder()
                        .description("Tenderize the chopped veggies")
                        .step(2)
                        .build()
        ));

        //when
        Recipe recipe = Recipe.builder()
                .id(UUID.randomUUID())
                .cookingTime(35)
                .preparationTime(10)
                .name("Macaroni and cheese")
                .servings(3)
                .instructions(instructions)
                .ingredients(ingredients)
                .build();

        //then
        assertThat(recipe.getIngredients()).hasSize(6);
        assertThat(recipe.getInstructions()).hasSize(4);
        assertThat(recipe.getInstructions().first().getStep()).isEqualTo(1);
        assertThat(recipe.getInstructions().last().getStep()).isEqualTo(4);
        assertThat(recipe.getName()).isEqualTo("Macaroni and cheese");
        assertThat(recipe.getServings()).isEqualTo(3);
        assertThat(recipe.getCookingTime()).isEqualTo(35);
        assertThat(recipe.getPreparationTime()).isEqualTo(10);
    }

    private Ingredient ingredient(String description) {
        return Ingredient.builder()
                .id(UUID.randomUUID())
                .description(description)
                .build();
    }

}