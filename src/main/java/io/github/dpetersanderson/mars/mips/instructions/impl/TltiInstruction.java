package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TltiInstruction extends BasicInstruction {
    public TltiInstruction() {
        super(
                "tlti $t1,-100",
                "Trap if less than immediate : Trap if $t1 less than sign-extended 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "000001 fffff 01010 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) < (operands[1] << 16 >> 16)) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
