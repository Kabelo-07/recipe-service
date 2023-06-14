package com.km.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class UpdateRecipeDTO {

    @JsonProperty
    private String name;

    @Min(1)
    @JsonProperty
    private Integer servings;

    @Min(1)
    @JsonProperty("preparation_time")
    private Integer preparationTime;

    @JsonProperty("category")
    private CategoryDTO categoryType;

    @JsonProperty("cooking_time")
    private Integer cookingTime;

}
