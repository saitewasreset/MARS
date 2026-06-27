package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TgeInstruction extends BasicInstruction {
    public TgeInstruction() {
        super(
                "tge $t1,$t2",
                "Trap if greater or equal : Trap if $t1 is greater than or equal to $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss ccccc ccccc 110000",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) >= RegisterFile.getValue(operands[1])) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
