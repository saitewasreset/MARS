package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SllvInstruction extends BasicInstruction {
    public SllvInstruction() {
        super(
                "sllv $t1,$t2,$t3",
                "Shift left logical variable : Set $t1 to result of shifting $t2 left by number of bits specified by"
                        + " value in low-order 5 bits of $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 ttttt sssss fffff 00000 000100",
                statement -> {
                    int[] operands = statement.getOperands();
                    // Mask all but low 5 bits of register containing shamt.
                    RegisterFile.updateRegister(
                            operands[0],
                            RegisterFile.getValue(operands[1]) << (RegisterFile.getValue(operands[2]) & 0x0000001F));
                });
    }
}
