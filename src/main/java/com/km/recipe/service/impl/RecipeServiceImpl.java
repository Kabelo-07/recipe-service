package com.km.recipe.service.impl;

import com.km.recipe.domain.Ingredient;
import com.km.recipe.domain.Instruction;
import com.km.recipe.domain.Recipe;
import com.km.recipe.domain.repository.RecipeRepository;
import com.km.recipe.dto.*;
import com.km.recipe.dto.page.RecipePage;
import com.km.recipe.exceptions.InvalidCreationRequestException;
import com.km.recipe.exceptions.InvalidRequestException;
import com.km.recipe.exceptions.RecipeNotFoundException;
import com.km.recipe.exceptions.RecipeViolationException;
import com.km.recipe.mappers.IngredientMapper;
import com.km.recipe.mappers.InstructionMapper;
import com.km.recipe.mappers.RecipeMapper;
import com.km.recipe.service.contract.IngredientService;
import com.km.recipe.service.contract.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository repository;
    private final RecipeMapper recipeMapper;
    private final IngredientService ingredientService;
    private final IngredientMapper ingredientMapper;
    private final InstructionMapper instructionMapper;

    @Override
    public RecipeDTO create(CreateRecipeDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new RecipeViolationException(dto.getName());
        }

        try {
            Recipe entity = recipeMapper.toEntity(dto);
            entity = repository.save(entity);
            return recipeMapper.toDto(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new RecipeViolationException(dto.getName());
        }
    }

    @Override
    public RecipeDTO update(UUID recipeId, UpdateRecipeDTO dto) {
        if (repository.existsByNameWithDifferentId(dto.getName(), recipeId)) {
            throw new RecipeViolationException(dto.getName());
        }

        Recipe recipe = findRecipeById(recipeId);

        Recipe updatedRecipe = recipeMapper.updateRecipe(recipe, dto);
        updatedRecipe = repository.save(updatedRecipe);
        return recipeMapper.toDto(updatedRecipe);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RecipeDTO findById(UUID recipeId) {
        Recipe recipe = findRecipeById(recipeId);
        return recipeMapper.toDto(recipe);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public RecipePage findAll(Specification<Recipe> specification, Pageable pageable) {
        Page<Recipe> recipePage = repository.findAll(specification, pageable);
        return RecipePage.toPage(recipePage);
    }

    @Override
    public void delete(UUID recipeId) {
        Recipe recipe = repository.findById(recipeId)
                .orElseThrow(() -> new InvalidRequestException(String.format("Cannot remove recipe with id: %s", recipeId)));
        repository.delete(recipe);
    }

    @Override
    public void deleteInstructions(UUID recipeId, List<UUID> instructionIds) {
        Recipe recipe = findRecipeById(recipeId);
        recipe.getInstructions().removeIf(instruction -> instructionIds.contains(instruction.getId()));
        repository.save(recipe);
    }

    @Override
    public void deleteIngredients(UUID recipeId, List<UUID> ingredientIds) {
        Recipe recipe = findRecipeById(recipeId);
        recipe.getIngredients().removeIf(ingredient -> ingredientIds.contains(ingredient.getId()));
        repository.save(recipe);
    }

    @Override
    public RecipeDTO updateIngredients(UUID recipeId, List<IngredientDTO> dtos) {
        Recipe recipe = findRecipeById(recipeId);

        Map<UUID, IngredientDTO> collect = dtos.stream()
                .collect(Collectors.toMap(IngredientDTO::getId, dto -> dto));

        recipe.getIngredients().forEach(ingredient -> {
            IngredientDTO dto = collect.get(ingredient.getId());
            if (null != dto) {
                ingredient.setDescription(dto.getDescription());
            }
        });

        recipe = repository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    @Override
    public RecipeDTO addIngredients(UUID recipeId, List<IngredientDTO> dtos) {
        Recipe recipe = findRecipeById(recipeId);
        boolean anyMatchWithId = dtos.stream().anyMatch(dto -> Objects.nonNull(dto.getId()));

        if (anyMatchWithId) {
            throw new InvalidCreationRequestException("Cannot pass Ids when adding ingredients");
        }

        Set<Ingredient> ingredients = dtos.stream()
                .map(ingredientMapper::toEntity)
                .collect(Collectors.toSet());

        recipe.getIngredients().addAll(ingredients);
        recipe = repository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    @Override
    public RecipeDTO updateInstructions(UUID recipeId, List<InstructionDTO> dtos) {
        Recipe recipe = findRecipeById(recipeId);

        Map<UUID, InstructionDTO> map = dtos.stream()
                .collect(Collectors.toMap(InstructionDTO::getId, dto -> dto));

        recipe.getInstructions().forEach(instruction -> {
            InstructionDTO dto = map.get(instruction.getId());
            instructionMapper.update(dto, instruction);
        });

        recipe = repository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    @Override
    public RecipeDTO addInstructions(UUID recipeId, List<InstructionDTO> dtos) {
        Recipe recipe = findRecipeById(recipeId);
        boolean anyMatchWithId = dtos.stream().anyMatch(dto -> Objects.nonNull(dto.getId()));

        if (anyMatchWithId) {
            throw new InvalidCreationRequestException("Cannot pass Ids when adding instructions");
        }

        Set<Instruction> instructions = dtos.stream()
                .map(instructionMapper::toEntity)
                .collect(Collectors.toSet());

        recipe.getInstructions().addAll(instructions);
        recipe = repository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    private Recipe findRecipeById(UUID recipeId) {
        return repository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException(recipeId));
    }
}
