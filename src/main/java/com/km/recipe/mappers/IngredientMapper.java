package com.km.recipe.mappers;


import com.km.recipe.domain.Ingredient;
import com.km.recipe.dto.IngredientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IngredientMapper extends AbstractMapper<IngredientDTO, Ingredient> {

    @Override
    Set<IngredientDTO> toDto(Collection<Ingredient> e);

    @Override
    IngredientDTO toDto(Ingredient ingredient);

    @Override
    Ingredient toEntity(IngredientDTO ingredientDTO);

    @Override
    Set<Ingredient> toEntity(Collection<IngredientDTO> dtos);
}
