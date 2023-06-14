package com.km.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class RecipeModel {

    @NotBlank
    private String name;

    @Min(1)
    @Positive
    @NotNull
    private Integer servings;

    @Min(1)
    @Positive
    @NotNull
    @JsonProperty("preparation_time")
    private Integer preparationTime;

    @NotNull
    @JsonProperty("category")
    private CategoryDTO categoryType;

    @Min(2)
    @JsonProperty("cooking_time")
    private int cookingTime;

    @Builder.Default
    @NotEmpty
    private List<InstructionDTO> instructions = List.of();

    @Builder.Default
    @NotEmpty
    private List<IngredientDTO> ingredients = List.of();

}
