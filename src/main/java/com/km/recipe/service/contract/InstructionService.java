package com.km.recipe.service.contract;

import com.km.recipe.dto.InstructionDTO;

import java.util.Collection;
import java.util.UUID;

public interface InstructionService {

    void updateInstructions(UUID recipeId, Collection<InstructionDTO> dtos);

    void deleteInstructions(UUID recipeId, Collection<UUID> ids);
}
