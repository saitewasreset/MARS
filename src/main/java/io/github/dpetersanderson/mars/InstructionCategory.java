package io.github.dpetersanderson.mars;

public enum InstructionCategory {
    DIV("Divide", 15),
    MULT("Multiply", 5),
    JUMP("Jump", 2),
    BRANCH("Branch", 2),
    MEM("Memory", 3),
    OTHER("Other", 1);

    private final String name;
    private final int weight;

    InstructionCategory(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }
}
