package com.km.recipe.domain.repository;

import com.km.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {

    @Query(value = "SELECT r FROM Recipe r WHERE UPPER(r.name) = UPPER(:name)")
    Optional<Recipe> findByName(@Param("name") String name);

    @Query(value = "SELECT CASE WHEN count(r) > 0 THEN true ELSE false END FROM Recipe r WHERE UPPER(r.name) = UPPER(:name)")
    boolean existsByName(@Param("name") String name);

    @Query(value = "SELECT CASE WHEN count(r) > 0 THEN true ELSE false END FROM Recipe r WHERE UPPER(r.name) = UPPER(:name) " +
            "AND r.id <> :id")
    boolean existsByNameWithDifferentId(@Param("name") String name, @Param("id") UUID id);
}
