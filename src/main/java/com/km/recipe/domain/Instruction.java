package com.km.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "recipe_instructions", uniqueConstraints = {
        @UniqueConstraint(name = "uq_instruction_recipe_id_step", columnNames = {"recipe_id", "step"}),
        @UniqueConstraint(name = "uq_instruction_recipe_id_description", columnNames = {"recipe_id", "description"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Instruction extends AbstractEntity implements Comparable<Instruction> {

    @Column(name = "recipe_id", nullable = false, insertable = false, updatable = false)
    private UUID recipeId;

    @Column(nullable = false)
    @Convert(converter = NameConverter.class)
    private String description;

    @Column(name = "detail_description")
    private String detailedDescription;

    @Min(1)
    @Column(nullable = false)
    private Integer step;

    @Override
    public int compareTo(Instruction o) {
        return this.step.compareTo(o.step);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Instruction.class.getSimpleName() + "[", "]")
                .add("description='" + description + "'")
                .add("detailedDescription='" + detailedDescription + "'")
                .add("step=" + step)
                .add("id=" + id)
                .toString();
    }
}
