package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class Teq2Instruction extends BasicInstruction {
    public Teq2Instruction() {
        super(
                "teq $t1,$t2,100",
                "Trap if equal with code : Trap if $t1 is equal to $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss ttttt ttttt 110100",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                        throw new ProcessingException(statement, "trap", Exceptions.TRAP_EXCEPTION);
                    }
                });
    }
}
