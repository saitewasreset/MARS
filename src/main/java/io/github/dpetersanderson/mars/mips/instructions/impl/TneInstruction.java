package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TneInstruction extends BasicInstruction {
    public TneInstruction() {
        super(
                "tne $t1,$t2",
                "Trap if not equal : Trap if $t1 is not equal to $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss ccccc ccccc 110110",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
