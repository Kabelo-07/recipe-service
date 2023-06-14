package com.km.recipe.mappers;


import com.km.recipe.domain.Instruction;
import com.km.recipe.dto.InstructionDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InstructionMapper extends AbstractMapper<InstructionDTO, Instruction> {

    @Override
    Instruction toEntity(InstructionDTO instructionDTO);

    @Override
    InstructionDTO toDto(Instruction instruction);

    @Override
    Set<InstructionDTO> toDto(Collection<Instruction> e);

    default void update(InstructionDTO dto, Instruction instruction) {
        if (null == dto) {
            return;
        }

        if (ObjectUtils.isNotEmpty(dto.getStep())) {
            instruction.setStep(dto.getStep());
        }

        if (ObjectUtils.isNotEmpty(dto.getDescription())) {
            instruction.setDescription(dto.getDescription());
        }

        if (ObjectUtils.isNotEmpty(dto.getDetailedDescription())) {
            instruction.setDetailedDescription(dto.getDetailedDescription());
        }
    }
}
