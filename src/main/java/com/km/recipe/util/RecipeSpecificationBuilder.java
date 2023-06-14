package com.km.recipe.util;

import com.km.recipe.domain.*;
import com.km.recipe.dto.CategoryDTO;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class RecipeSpecificationBuilder extends AbstractSpecBuilder<Recipe> {

    public RecipeSpecificationBuilder withRecipeId(UUID id) {
        Optional.ofNullable(id).ifPresent(uuid -> root = root.and(withIdEquals(uuid)));
        return this;
    }

    public RecipeSpecificationBuilder withServings(Integer numberOfServings) {
        Optional.ofNullable(numberOfServings)
                .filter(integer -> integer > 0)
                .ifPresent(integer -> root = root.and(withServingsEquals(integer)));
        return this;
    }

    public RecipeSpecificationBuilder withExcludedIngredients(List<String> ingredients) {
        if (!CollectionUtils.isEmpty(ingredients)) {
            root = root.and(ingredientsExcludes(ingredients));
        }
        return this;
    }

    public RecipeSpecificationBuilder withIncludedIngredients(List<String> ingredients) {
        if (!CollectionUtils.isEmpty(ingredients)) {
            root = root.and(ingredientsIncludes(ingredients));
        }
        return this;
    }

    public RecipeSpecificationBuilder withInstructions(List<String> instructions) {
        if (!CollectionUtils.isEmpty(instructions)) {
            root = root.and(instructionsIncludes(instructions));
        }
        return this;
    }

    public RecipeSpecificationBuilder withCategory(CategoryDTO categoryDTO) {
        Optional.ofNullable(categoryDTO)
                .ifPresent(value -> root = root.and(withCategoryEquals(value)));
        return this;
    }

    static Specification<Recipe> withIdEquals(UUID id) {
        return (root, cq, cb) -> cb.equal(root.get(AbstractEntity_.id), id);
    }

    static Specification<Recipe> withServingsEquals(Integer numberOfServings) {
        return (root, query, cb) -> cb.equal(root.get(Recipe_.servings), numberOfServings);
    }

    static Specification<Recipe> ingredientsExcludes(List<String> ingredients) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            ingredients.forEach(s -> predicates.add(ingredientPredicate(cb, root, s, true)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    static Specification<Recipe> ingredientsIncludes(List<String> ingredients) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            ingredients.forEach(s -> predicates.add(ingredientPredicate(cb, root, s, false)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    static Specification<Recipe> instructionsIncludes(List<String> instructions) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            instructions.forEach(s -> predicates.add(instructionsPredicate(cb, root, s)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate ingredientPredicate(CriteriaBuilder cb, Root<Recipe> root, String value, boolean notLike) {
        Join<Ingredient, Recipe> ingredients = root.join("ingredients");
        String queryValue = "%" + StringUtils.lowerCase(value) + "%";
        Expression<String> expression = cb.lower(ingredients.get("description"));
        if (notLike) {
            return cb.like(expression, queryValue).not();
        }
        return cb.like(expression, queryValue);
    }

    private static Predicate instructionsPredicate(CriteriaBuilder cb, Root<Recipe> root, String value) {
        Join<Instruction, Recipe> ingredients = root.join("instructions");
        String queryValue = "%" + StringUtils.lowerCase(value) + "%";
        Expression<String> expression = cb.lower(ingredients.get("description"));
        return cb.like(expression, queryValue);
    }

    static Specification<Recipe> withCategoryEquals(CategoryDTO categoryDTO) {
        return (root, query, cb) -> cb.equal(root.get(Recipe_.categoryType), CategoryType.valueOf(categoryDTO.name()));
    }

}
