package io.github.dpetersanderson.mars;

import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessListener;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import io.github.dpetersanderson.mars.util.InstructionStatisticsCounter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CliInstructionStatisticsListener implements MemoryAccessListener {
    public record StatisticsItem(BigDecimal instructionCount, BigDecimal weight, BigDecimal weightedCycles) {}

    public record StatisticsResult(
            BigDecimal finalCycles, BigDecimal totalInstructionCount, Map<String, StatisticsItem> items) {}

    private final InstructionStatisticsCounter instructionStatisticsCounter = new InstructionStatisticsCounter();

    @Override
    public void memoryAccessed(MemoryAccessNotice notice) {
        instructionStatisticsCounter.processMIPSUpdate(notice);
    }

    public StatisticsResult getStatistics() {
        BigDecimal finalCycles = BigDecimal.ZERO;
        BigDecimal totalInstructions = BigDecimal.ZERO;

        Map<InstructionCategory, BigDecimal> instructionCounts = instructionStatisticsCounter.getInstructionCounts();
        Map<InstructionCategory, BigDecimal> weightedCycles = instructionStatisticsCounter.getWeightedCycles();

        for (InstructionCategory category : InstructionCategory.values()) {
            finalCycles = finalCycles.add(weightedCycles.get(category));
            totalInstructions = totalInstructions.add(instructionCounts.get(category));
        }

        Map<String, StatisticsItem> items = new HashMap<>();
        for (InstructionCategory category : InstructionCategory.values()) {
            StatisticsItem statisticsItem = new StatisticsItem(
                    instructionCounts.get(category), category.getWeight(), weightedCycles.get(category));
            items.put(category.getName(), statisticsItem);
        }

        return new StatisticsResult(finalCycles, totalInstructions, items);
    }
}
