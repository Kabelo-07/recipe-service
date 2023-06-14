package com.km.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.UUID;

@SuperBuilder
@Getter
@NoArgsConstructor
public class InstructionDTO implements Comparable<InstructionDTO> {

    private UUID id;

    @NotBlank
    private String description;

    @JsonProperty("detailed_description")
    private String detailedDescription;

    @Min(1)
    @NotNull
    private Integer step;

    @Override
    public int compareTo(InstructionDTO o) {
        return this.description.compareTo(o.description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstructionDTO)) return false;
        InstructionDTO that = (InstructionDTO) o;
        return getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription());
    }
}
