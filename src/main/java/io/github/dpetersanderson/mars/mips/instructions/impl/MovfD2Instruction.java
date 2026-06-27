package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovfD2Instruction extends BasicInstruction {
    public MovfD2Instruction() {
        super(
                "movf.d $f2,$f4,1",
                "Move floating point double precision : If condition flag specified by immediate is false, set double"
                        + " precision $f2 to double precision value in $f4",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 ttt 00 sssss fffff 010001",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    if (Coprocessor1.getConditionFlag(operands[2]) == 0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0] + 1, Coprocessor1.getValue(operands[1] + 1));
                    }
                });
    }
}
