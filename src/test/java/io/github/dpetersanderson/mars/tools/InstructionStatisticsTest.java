package io.github.dpetersanderson.mars.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class InstructionStatisticsTest {
    @Test
    void buildsFinalCyclesFormulaWithBigDecimalWeights() {
        assertEquals(
                "FinalCycle = Divide * 15 + Multiply * 5 + Jump * 2 + Branch * 2 + Memory * 3 + Other * 1",
                InstructionStatistics.buildFinalCyclesFormula());
    }

    @Test
    void calculatesProgressAfterScalingTheRatio() {
        assertEquals(250, InstructionStatistics.calculateProgressBarValue(BigDecimal.ONE, BigDecimal.valueOf(4)));
        assertEquals(333, InstructionStatistics.calculateProgressBarValue(BigDecimal.ONE, BigDecimal.valueOf(3)));
    }

    @Test
    void returnsZeroProgressForZeroTotal() {
        assertEquals(0, InstructionStatistics.calculateProgressBarValue(BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
