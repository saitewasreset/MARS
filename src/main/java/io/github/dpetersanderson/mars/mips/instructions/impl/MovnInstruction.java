package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovnInstruction extends BasicInstruction {
    public MovnInstruction() {
        super(
                "movn $t1,$t2,$t3",
                "Move conditional not zero : Set $t1 to $t2 if $t3 is not zero",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 001011",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[2]) != 0)
                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                });
    }
}
