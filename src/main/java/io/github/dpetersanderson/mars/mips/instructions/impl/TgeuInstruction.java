package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class TgeuInstruction extends BasicInstruction {
    public TgeuInstruction() {
        super(
                "tgeu $t1,$t2",
                "Trap if greater or equal unsigned : Trap if $t1 is greater than or equal to $t2 using unsigned"
                        + " comparision",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss ccccc ccccc 110001",
                statement -> {
                    int[] operands = statement.getOperands();
                    int first = RegisterFile.getValue(operands[0]);
                    int second = RegisterFile.getValue(operands[1]);
                    // if signs same, do straight compare; if signs differ & first negative then first greater else
                    // second
                    if ((first >= 0 && second >= 0 || first < 0 && second < 0) ? (first >= second) : (first < 0)) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
