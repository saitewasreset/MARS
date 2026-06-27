package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class AndInstruction extends BasicInstruction {
    public AndInstruction() {
        super(
                "and $t1,$t2,$t3",
                "Bitwise AND : Set $t1 to bitwise AND of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100100",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) & RegisterFile.getValue(operands[2]));
                });
    }
}
