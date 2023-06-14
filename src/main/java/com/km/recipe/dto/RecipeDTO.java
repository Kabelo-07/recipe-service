package com.km.recipe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
public class RecipeDTO extends RecipeModel {

    @NotNull
    private UUID id;
}
