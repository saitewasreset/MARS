package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class CLeSInstruction extends BasicInstruction {
    public CLeSInstruction() {
        super(
                "c.le.s $f0,$f1",
                "Compare less or equal single precision : If $f0 is less than or equal to $f1, set Coprocessor 1"
                        + " condition flag 0 true else set it false",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 sssss fffff 00000 111110",
                statement -> {
                    int[] operands = statement.getOperands();
                    float op1 = Float.intBitsToFloat(Coprocessor1.getValue(operands[0]));
                    float op2 = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    if (op1 <= op2) Coprocessor1.setConditionFlag(0);
                    else Coprocessor1.clearConditionFlag(0);
                });
    }
}
