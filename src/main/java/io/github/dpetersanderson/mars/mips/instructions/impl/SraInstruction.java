package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SraInstruction extends BasicInstruction {
    public SraInstruction() {
        super(
                "sra $t1,$t2,10",
                "Shift right arithmetic : Set $t1 to result of sign-extended shifting $t2 right by number of bits"
                        + " specified by immediate",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 sssss fffff ttttt 000011",
                statement -> {
                    int[] operands = statement.getOperands();
                    // must sign-fill, so use ">>".
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]) >> operands[2]);
                });
    }
}
