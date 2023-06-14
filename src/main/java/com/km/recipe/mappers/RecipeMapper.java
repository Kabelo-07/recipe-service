package com.km.recipe.mappers;

import com.km.recipe.domain.CategoryType;
import com.km.recipe.domain.Recipe;
import com.km.recipe.dto.CategoryDTO;
import com.km.recipe.dto.CreateRecipeDTO;
import com.km.recipe.dto.RecipeDTO;
import com.km.recipe.dto.UpdateRecipeDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface RecipeMapper extends AbstractMapper<RecipeDTO, Recipe> {

    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    @Override
    Recipe toEntity(RecipeDTO recipeDTO);

    Recipe toEntity(CreateRecipeDTO recipeDTO);

    @Override
    RecipeDTO toDto(Recipe recipe);

    @Override
    Set<RecipeDTO> toDto(Collection<Recipe> e);

    default CategoryType toCategoryType(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        return switch (categoryDTO) {
            case VEGETARIAN -> CategoryType.VEGETARIAN;
            case COMFORT -> CategoryType.COMFORT;
            case BEEF -> CategoryType.BEEF;
            case CHICKEN -> CategoryType.CHICKEN;
            case OTHER -> CategoryType.OTHER;
            default -> throw new IllegalArgumentException("Unexpected enum constant: " + categoryDTO);
        };
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Recipe updateRecipe(@MappingTarget Recipe recipe, UpdateRecipeDTO updateRecipeDTO);

}
