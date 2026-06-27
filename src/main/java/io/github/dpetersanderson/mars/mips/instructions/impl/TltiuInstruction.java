package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TltiuInstruction extends BasicInstruction {
    public TltiuInstruction() {
        super(
                "tltiu $t1,-100",
                "Trap if less than immediate unsigned : Trap if $t1 less than sign-extended 16-bit immediate, unsigned"
                        + " comparison",
                BasicInstructionFormat.I_FORMAT,
                "000001 fffff 01011 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    int first = RegisterFile.getValue(operands[0]);
                    // 16 bit immediate value in operands[1] is sign-extended
                    int second = operands[1] << 16 >> 16;
                    // if signs same, do straight compare; if signs differ & first positive then first is less else
                    // second
                    if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first < second) : (first >= 0)) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
