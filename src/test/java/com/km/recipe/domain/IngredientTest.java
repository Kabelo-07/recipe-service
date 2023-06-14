package com.km.recipe.domain;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class IngredientTest {

    @Test
    void ingredientsWithSameId_willBeEqual() {
        //given
        UUID uuid = UUID.randomUUID();

        Ingredient one = Ingredient.builder()
                .id(uuid)
                .description("One")
                .build();

        Ingredient two = Ingredient.builder()
                .id(uuid)
                .description("One")
                .build();

        //then
        assertThat(one).isEqualTo(two);
    }

    @Test
    void ingredientsWithDifferentIdAndDescription_willNotBeEqual() {
        //given
        Ingredient one = Ingredient.builder()
                .id(UUID.randomUUID())
                .description("One")
                .build();

        Ingredient two = Ingredient.builder()
                .id(UUID.randomUUID())
                .description("Two")
                .build();

        //then
        assertThat(one).isNotEqualTo(two);
    }

    @Test
    void listOfIngredients_willBeSorted_inAscendingOrder_byDescription() {
        //given
        Ingredient orange = Ingredient.builder()
                .id(UUID.randomUUID())
                .description("Oranges")
                .build();

        Ingredient tomatoes = Ingredient.builder()
                .id(UUID.randomUUID())
                .description("Tomatoes")
                .build();

        Ingredient apples = Ingredient.builder()
                .id(UUID.randomUUID())
                .description("Apples")
                .build();

        //when
        SortedSet<Ingredient> ingredients = new TreeSet<>(List.of(orange, tomatoes, apples));

        //then
        assertThat(ingredients).hasSize(3);
        Ingredient ingredient = ingredients.iterator().next();

        assertThat(ingredient).isEqualTo(apples);
    }

    @Test
    void listOfIngredients_willNotContain_duplicates() {
        //given
        UUID uuid = UUID.randomUUID();

        Ingredient orange = Ingredient.builder()
                .id(uuid)
                .description("Oranges")
                .build();

        Ingredient tomatoes = Ingredient.builder()
                .id(uuid)
                .description("Tomatoes")
                .build();

        //when
        Set<Ingredient> ingredients = new HashSet<>(List.of(orange, tomatoes));

        //then
        assertThat(ingredients).hasSize(1);
    }

}
