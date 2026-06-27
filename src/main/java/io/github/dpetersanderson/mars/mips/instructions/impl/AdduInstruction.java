package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class AdduInstruction extends BasicInstruction {
    public AdduInstruction() {
        super(
                "addu $t1,$t2,$t3",
                "Addition unsigned without overflow : set $t1 to ($t2 plus $t3), no overflow",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100001",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) + RegisterFile.getValue(operands[2]));
                });
    }
}
