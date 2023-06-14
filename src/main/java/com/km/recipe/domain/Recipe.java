package com.km.recipe.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Entity
@Table(name = "recipe")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Recipe extends AbstractAuditableEntity {

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull
    private CategoryType categoryType;

    @Min(1)
    @Column(nullable = false, name = "number_of_servings")
    private Integer servings;

    /**
     * <p>Preparation time is the number of minutes it takes to prepare</p>
     */
    @Min(1)
    @NotNull
    @Column(nullable = false, name = "preparation_time")
    private Integer preparationTime;

    /**
     * <p>Cooking time is the number of minutes it takes for cooking</p>
     */
    @Min(1)
    @NotNull
    @Column(nullable = false, name = "cooking_time")
    private Integer cookingTime;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "recipe_id", referencedColumnName = "id")
    @Builder.Default
    @NotEmpty
    private SortedSet<Instruction> instructions = new TreeSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "recipe_id", referencedColumnName = "id")
    @Builder.Default
    @NotEmpty
    private SortedSet<Ingredient> ingredients = new TreeSet<>();

    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.name = StringUtils.capitalize(StringUtils.lowerCase(this.name));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Recipe.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("categoryType=" + categoryType)
                .add("servings=" + servings)
                .add("preparationTime=" + preparationTime)
                .add("cookingTime=" + cookingTime)
                .add("instructions=" + instructions)
                .add("ingredients=" + ingredients)
                .add("id=" + id)
                .add("createdDate=" + createdDate)
                .add("updatedDate=" + updatedDate)
                .toString();
    }
}
