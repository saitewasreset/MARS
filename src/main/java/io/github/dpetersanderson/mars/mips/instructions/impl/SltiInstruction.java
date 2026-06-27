package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SltiInstruction extends BasicInstruction {
    public SltiInstruction() {
        super(
                "slti $t1,$t2,-100",
                "Set less than immediate : If $t2 is less than sign-extended 16-bit immediate, then set $t1 to 1 else"
                        + " set $t1 to 0",
                BasicInstructionFormat.I_FORMAT,
                "001010 sssss fffff tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    // 16 bit immediate value in operands[2] is sign-extended
                    RegisterFile.updateRegister(
                            operands[0], (RegisterFile.getValue(operands[1]) < (operands[2] << 16 >> 16)) ? 1 : 0);
                });
    }
}
