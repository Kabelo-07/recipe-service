package com.km.recipe.service.impl;

import com.km.recipe.domain.Ingredient;
import com.km.recipe.domain.repository.IngredientRepository;
import com.km.recipe.dto.IngredientDTO;
import com.km.recipe.exceptions.InvalidCreationRequestException;
import com.km.recipe.exceptions.InvalidRequestException;
import com.km.recipe.mappers.IngredientMapper;
import com.km.recipe.service.contract.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository repository;
    private final IngredientMapper ingredientMapper;

    @Override
    public void addIngredients(UUID recipeId, Collection<IngredientDTO> dtos) {
        boolean anyMatchWithId = dtos.stream().anyMatch(dto -> Objects.nonNull(dto.getId()));

        if (anyMatchWithId) {
            throw new InvalidCreationRequestException("Cannot pass Ids when adding ingredients");
        }

        Set<Ingredient> ingredients = dtos.stream().map(dto -> Ingredient.builder()
                .description(dto.getDescription())
                .build())
                .collect(Collectors.toSet());

        repository.saveAll(ingredients);
    }

    @Override
    public void updateIngredients(UUID recipeId, Collection<IngredientDTO> dtos) {
        Set<UUID> uuidSet = dtos.stream()
                .map(IngredientDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Ingredient> existingIngredients = repository.findAllByRecipeIdAndIdsIn(recipeId, uuidSet);

        if (existingIngredients.isEmpty()) {
            throw new InvalidRequestException("Invalid ingredients update request");
        }

        Map<UUID, IngredientDTO> collect = dtos.stream().collect(Collectors.toMap(IngredientDTO::getId, dto -> dto));
        Set<Ingredient> ingredientSet = existingIngredients.stream()
                .map(ingredient -> ingredientMapper.toEntity(collect.get(ingredient.getId())))
                .collect(Collectors.toSet());
        repository.saveAll(ingredientSet);
    }

    @Override
    public void deleteIngredients(UUID recipeId, Collection<UUID> ingredientIds) {
        Set<Ingredient> ingredients = repository.findAllByRecipeIdAndIdsIn(recipeId, ingredientIds);
        repository.deleteAll(ingredients);
    }
}
