package com.km.recipe.domain;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class InstructionTest {

    @Test
    void instructions_withSameId_willBeEqual() {
        //given
        UUID id = UUID.randomUUID();

        Instruction instructionOne = Instruction.builder()
                .id(id)
                .description("Put into the oven")
                .step(1)
                .build();

        //and
        Instruction instructionTwo = Instruction.builder()
                .id(id)
                .description("Put into the oven")
                .step(2)
                .build();

        //then
        assertThat(instructionOne).isEqualTo(instructionTwo);
    }

    @Test
    void instructions_withDifferentIdAndDescription_willNotBeEqual() {
        //given
        Instruction instructionOne = Instruction.builder()
                .id(UUID.randomUUID())
                .description("Add two table spoons of salt")
                .step(1)
                .build();

        //and
        Instruction instructionTwo = Instruction.builder()
                .id(UUID.randomUUID())
                .description("Add the marinate sauce")
                .step(2)
                .build();

        //then
        assertThat(instructionOne).isNotEqualTo(instructionTwo);
    }

    @Test
    void listOfInstructions_willBeSorted_inAscendingOrder_byStepNumber() {
        //given
        Instruction instructionOne = Instruction.builder()
                .id(UUID.randomUUID())
                .description("Combine the vegetables")
                .detailedDescription("Combine the vegetables and add some soy sauce")
                .step(1)
                .build();

        //and
        Instruction instructionTwo = Instruction.builder()
                .id(UUID.randomUUID())
                .description("Add in the spice after 5 minutes")
                .step(2)
                .build();

        //when
        Set<Instruction> instructions = new TreeSet<>(List.of(instructionTwo, instructionOne));

        //then
        assertThat(instructions).hasSize(2);
        Instruction instruction = instructions.iterator().next();

        assertThat(instruction).isEqualTo(instructionOne);
        assertThat(instruction.getStep()).isEqualTo(1);
        assertThat(instruction.getDescription()).isEqualTo("Combine the vegetables");
        assertThat(instruction.getDetailedDescription()).isEqualTo("Combine the vegetables and add some soy sauce");
    }

    @Test
    void listOfInstructions_willNotContain_duplicates() {
        //given
        UUID uuid = UUID.randomUUID();

        Instruction orange = Instruction.builder()
                .id(uuid)
                .description("Oranges")
                .build();

        Instruction tomatoes = Instruction.builder()
                .id(uuid)
                .description("Tomatoes")
                .build();

        //when
        Set<Instruction> instructions = new HashSet<>(List.of(orange, tomatoes));

        //then
        assertThat(instructions).hasSize(1);
    }

}
