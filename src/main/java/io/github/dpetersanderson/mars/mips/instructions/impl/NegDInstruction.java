package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class NegDInstruction extends BasicInstruction {
    public NegDInstruction() {
        super(
                "neg.d $f2,$f4",
                "Floating point negate double precision : Set double precision $f2 to negation of double precision"
                        + " value in $f4",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 00000 sssss fffff 000111",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    // flip the sign bit of the second register (high order word) of the pair
                    int value = Coprocessor1.getValue(operands[1] + 1);
                    Coprocessor1.updateRegister(
                            operands[0] + 1, ((value < 0) ? (value & Integer.MAX_VALUE) : (value | Integer.MIN_VALUE)));
                    Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                });
    }
}
