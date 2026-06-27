package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TgeiuInstruction extends BasicInstruction {
    public TgeiuInstruction() {
        super(
                "tgeiu $t1,-100",
                "Trap if greater or equal to immediate unsigned : Trap if $t1 greater than or equal to sign-extended"
                        + " 16 bit immediate, unsigned comparison",
                BasicInstructionFormat.I_FORMAT,
                "000001 fffff 01001 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    int first = RegisterFile.getValue(operands[0]);
                    // 16 bit immediate value in operands[1] is sign-extended
                    int second = operands[1] << 16 >> 16;
                    // if signs same, do straight compare; if signs differ & first negative then first greater else
                    // second
                    if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first >= second) : (first < 0)) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
