package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class OrInstruction extends BasicInstruction {
    public OrInstruction() {
        super(
                "or $t1,$t2,$t3",
                "Bitwise OR : Set $t1 to bitwise OR of $t2 and $t3",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 100101",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) | RegisterFile.getValue(operands[2]));
                });
    }
}
