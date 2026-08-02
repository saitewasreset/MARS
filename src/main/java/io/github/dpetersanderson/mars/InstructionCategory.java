package io.github.dpetersanderson.mars;

import java.math.BigDecimal;

public enum InstructionCategory {
    DIV("Divide", BigDecimal.valueOf(15)),
    MULT("Multiply", BigDecimal.valueOf(5)),
    JUMP("Jump", BigDecimal.valueOf(2)),
    BRANCH("Branch", BigDecimal.valueOf(2)),
    MEM("Memory", BigDecimal.valueOf(3)),
    OTHER("Other", BigDecimal.valueOf(1));

    private final String name;
    private final BigDecimal weight;

    InstructionCategory(String name, BigDecimal weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
