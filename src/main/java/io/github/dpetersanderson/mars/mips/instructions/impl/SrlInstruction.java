package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SrlInstruction extends BasicInstruction {
    public SrlInstruction() {
        super(
                "srl $t1,$t2,10",
                "Shift right logical : Set $t1 to result of shifting $t2 right by number of bits specified by"
                        + " immediate",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 sssss fffff ttttt 000010",
                statement -> {
                    int[] operands = statement.getOperands();
                    // must zero-fill, so use ">>>" instead of ">>".
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]) >>> operands[2]);
                });
    }
}
