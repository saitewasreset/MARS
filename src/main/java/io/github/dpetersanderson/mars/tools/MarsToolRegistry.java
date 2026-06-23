package io.github.dpetersanderson.mars.tools;

import java.util.List;

public final class MarsToolRegistry {
    private static final List<MarsTool> tools = List.of(
            new BHTSimulator(),
            new BitmapDisplay(),
            new CacheSimulator(),
            new DigitalLabSim(),
            new FloatRepresentation(),
            new InstructionCounter(),
            new InstructionStatistics(),
            new IntroToTools(),
            new KeyboardAndDisplaySimulator(),
            new MarsBot(),
            new MemoryReferenceVisualization(),
            new MipsXray(),
            new ScavengerHunt(),
            new ScreenMagnifier());

    private MarsToolRegistry() {}

    public static List<MarsTool> getTools() {
        return tools;
    }
}
