package io.github.dpetersanderson.mars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.InstructionCategory;
import io.github.dpetersanderson.mars.ProgramStatement;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InstructionStatisticsCounterTest {
    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("instructionCategories")
    void categorizesBasicInstructions(String description, int binaryStatement, InstructionCategory expected) {
        ProgramStatement statement = new ProgramStatement(binaryStatement, 0);

        assertEquals(expected, InstructionStatisticsCounter.getInstructionCategory(statement));
    }

    private static Stream<Arguments> instructionCategories() {
        return Stream.of(
                Arguments.of("mult", 0x00000018, InstructionCategory.MULT),
                Arguments.of("multu", 0x00000019, InstructionCategory.MULT),
                Arguments.of("madd", 0x70000000, InstructionCategory.MULT),
                Arguments.of("maddu", 0x70000001, InstructionCategory.MULT),
                Arguments.of("mul", 0x70000002, InstructionCategory.MULT),
                Arguments.of("msub", 0x70000004, InstructionCategory.MULT),
                Arguments.of("msubu", 0x70000005, InstructionCategory.MULT),
                Arguments.of("mul.s", 0x46000002, InstructionCategory.MULT),
                Arguments.of("mul.d", 0x46200002, InstructionCategory.MULT),
                Arguments.of("div", 0x0000001A, InstructionCategory.DIV),
                Arguments.of("divu", 0x0000001B, InstructionCategory.DIV),
                Arguments.of("div.s", 0x46000003, InstructionCategory.DIV),
                Arguments.of("div.d", 0x46200003, InstructionCategory.DIV),
                Arguments.of("j", 0x08000000, InstructionCategory.JUMP),
                Arguments.of("jal", 0x0C000000, InstructionCategory.JUMP),
                Arguments.of("jr", 0x00000008, InstructionCategory.JUMP),
                Arguments.of("jalr", 0x00000009, InstructionCategory.JUMP),
                Arguments.of("bltz", 0x04000008, InstructionCategory.BRANCH),
                Arguments.of("bgez", 0x04010008, InstructionCategory.BRANCH),
                Arguments.of("bltzal", 0x04100008, InstructionCategory.BRANCH),
                Arguments.of("bgezal", 0x04110008, InstructionCategory.BRANCH),
                Arguments.of("beq", 0x10000000, InstructionCategory.BRANCH),
                Arguments.of("bne", 0x14000000, InstructionCategory.BRANCH),
                Arguments.of("blez", 0x18000000, InstructionCategory.BRANCH),
                Arguments.of("bgtz", 0x1C000000, InstructionCategory.BRANCH),
                Arguments.of("bc1f", 0x45000002, InstructionCategory.BRANCH),
                Arguments.of("bc1t", 0x45010002, InstructionCategory.BRANCH),
                Arguments.of("lb", 0x80000000, InstructionCategory.MEM),
                Arguments.of("lh", 0x84000000, InstructionCategory.MEM),
                Arguments.of("lwl", 0x88000000, InstructionCategory.MEM),
                Arguments.of("lw", 0x8C000000, InstructionCategory.MEM),
                Arguments.of("lbu", 0x90000000, InstructionCategory.MEM),
                Arguments.of("lhu", 0x94000000, InstructionCategory.MEM),
                Arguments.of("lwr", 0x98000000, InstructionCategory.MEM),
                Arguments.of("sb", 0xA0000000, InstructionCategory.MEM),
                Arguments.of("sh", 0xA4000000, InstructionCategory.MEM),
                Arguments.of("swl", 0xA8000000, InstructionCategory.MEM),
                Arguments.of("sw", 0xAC000000, InstructionCategory.MEM),
                Arguments.of("swr", 0xB8000000, InstructionCategory.MEM),
                Arguments.of("ll", 0xC0000000, InstructionCategory.MEM),
                Arguments.of("lwc1", 0xC4000000, InstructionCategory.MEM),
                Arguments.of("ldc1", 0xD4000000, InstructionCategory.MEM),
                Arguments.of("sc", 0xE0000000, InstructionCategory.MEM),
                Arguments.of("swc1", 0xE4000000, InstructionCategory.MEM),
                Arguments.of("sdc1", 0xF4000000, InstructionCategory.MEM),
                Arguments.of("add", 0x00000020, InstructionCategory.OTHER),
                Arguments.of("teqi", 0x040C0000, InstructionCategory.OTHER),
                Arguments.of("clz", 0x70000020, InstructionCategory.OTHER),
                Arguments.of("add.s", 0x46000000, InstructionCategory.OTHER));
    }
}
