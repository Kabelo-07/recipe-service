package com.km.recipe.dto.page;

import com.km.recipe.domain.Recipe;
import com.km.recipe.mappers.RecipeMapper;
import com.km.recipe.dto.RecipeDTO;
import org.springframework.data.domain.Page;

import java.util.Set;

public class RecipePage extends AbstractPage<RecipeDTO> {

    private RecipePage(Set<RecipeDTO> content,
                      Boolean empty,
                      Boolean first,
                      Boolean last,
                      Integer number,
                      Integer size, Long totalElements,
                      Integer totalPages) {
        super(content, empty, first, last, number, size, totalElements, totalPages);
    }

    public static RecipePage toPage(Page<Recipe> page) {
        return new RecipePage(RecipeMapper.INSTANCE.toDto(page.getContent()),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

}

