package com.km.recipe.domain.repository;

import com.km.recipe.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {

    Set<Ingredient> findAllByRecipeId(UUID recipeId);

    @Query(value = "SELECT i FROM Ingredient i WHERE i.recipeId = :recipeId AND " +
            "i.id IN (:ids)")
    Set<Ingredient> findAllByRecipeIdAndIdsIn(@Param("recipeId") UUID recipeId,
                                              @Param("ids") Collection<UUID> ids);
}
