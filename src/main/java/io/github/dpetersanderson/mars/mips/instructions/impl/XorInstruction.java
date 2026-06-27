package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class XorInstruction extends BasicInstruction {
    public XorInstruction() {
        super(
                "xor $t1,$t2,$t3",
                "Bitwise XOR (exclusive OR) : Set $t1 to bitwise XOR of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100110",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) ^ RegisterFile.getValue(operands[2]));
                });
    }
}
