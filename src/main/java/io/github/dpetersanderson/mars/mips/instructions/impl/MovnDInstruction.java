package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovnDInstruction extends BasicInstruction {
    public MovnDInstruction() {
        super(
                "movn.d $f2,$f4,$t3",
                "Move floating point double precision : If $t3 is not zero, set double precision $f2 to double"
                        + " precision value in $f4",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 ttttt sssss fffff 010011",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    if (RegisterFile.getValue(operands[2]) != 0) {
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                        Coprocessor1.updateRegister(operands[0] + 1, Coprocessor1.getValue(operands[1] + 1));
                    }
                });
    }
}
