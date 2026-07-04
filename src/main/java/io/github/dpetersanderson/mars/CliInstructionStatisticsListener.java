package io.github.dpetersanderson.mars;

import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessListener;
import io.github.dpetersanderson.mars.mips.hardware.MemoryAccessNotice;
import io.github.dpetersanderson.mars.util.InstructionStatisticsCounter;

import java.util.HashMap;
import java.util.Map;

public class CliInstructionStatisticsListener implements MemoryAccessListener {
    public record StatisticsItem(int instructionCount, int weight, int weightedCycles) {}

    public record StatisticsResult(int finalCycles, int totalInstructionCount, Map<String, StatisticsItem> items) {}

    private final InstructionStatisticsCounter instructionStatisticsCounter = new InstructionStatisticsCounter();

    @Override
    public void memoryAccessed(MemoryAccessNotice notice) {
        instructionStatisticsCounter.processMIPSUpdate(notice);
    }

    public StatisticsResult getStatistics() {
        int finalCycles = 0;
        int totalInstructions = 0;

        Map<InstructionCategory, Integer> instructionCounts = instructionStatisticsCounter.getInstructionCounts();
        Map<InstructionCategory, Integer> weightedCycles = instructionStatisticsCounter.getWeightedCycles();

        for (InstructionCategory category : InstructionCategory.values()) {
            finalCycles += weightedCycles.get(category);
            totalInstructions += instructionCounts.get(category);
        }

        Map<String, StatisticsItem> items = new HashMap<>();
        for (InstructionCategory category : InstructionCategory.values()) {
            StatisticsItem statisticsItem = new StatisticsItem(
                    instructionCounts.get(category),
                    category.getWeight(),
                    weightedCycles.get(category));
            items.put(category.getName(), statisticsItem);
        }

        return new StatisticsResult(finalCycles, totalInstructions, items);
    }
}
