package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SllInstruction extends BasicInstruction {
    public SllInstruction() {
        super(
                "sll $t1,$t2,10",
                "Shift left logical : Set $t1 to result of shifting $t2 left by number of bits specified by immediate",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 sssss fffff ttttt 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]) << operands[2]);
                });
    }
}
