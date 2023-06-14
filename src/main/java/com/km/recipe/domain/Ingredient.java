package com.km.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "recipe_ingredients", uniqueConstraints = {
        @UniqueConstraint(name = "uq_ingredient_recipe_id_description", columnNames = {"recipe_id", "description"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Ingredient extends AbstractEntity implements Comparable<Ingredient> {

    @Column(name = "recipe_id", nullable = false, insertable = false, updatable = false)
    private UUID recipeId;

    @Convert(converter = NameConverter.class)
    @Column(nullable = false)
    private String description;

    @Override
    public int compareTo(Ingredient o) {
        return this.description.compareTo(o.description);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Ingredient.class.getSimpleName() + "[", "]")
                .add("description='" + description + "'")
                .add("id=" + id)
                .toString();
    }
}
