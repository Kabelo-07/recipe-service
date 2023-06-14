package com.km.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.UUID;

@SuperBuilder
@Getter
@NoArgsConstructor
public class IngredientDTO implements Comparable<IngredientDTO> {

    private UUID id;

    @NotBlank
    private String description;

    @Override
    public int compareTo(IngredientDTO o) {
        return this.description.compareTo(o.description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngredientDTO)) return false;
        IngredientDTO that = (IngredientDTO) o;
        return getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription());
    }
}
