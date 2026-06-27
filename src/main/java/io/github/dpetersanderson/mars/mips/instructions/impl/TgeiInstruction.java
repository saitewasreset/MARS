package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TgeiInstruction extends BasicInstruction {
    public TgeiInstruction() {
        super(
                "tgei $t1,-100",
                "Trap if greater than or equal to immediate : Trap if $t1 greater than or equal to sign-extended 16"
                        + " bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000001 fffff 01000 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) >= (operands[1] << 16 >> 16)) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
