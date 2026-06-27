package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SltiuInstruction extends BasicInstruction {
    public SltiuInstruction() {
        super(
                "sltiu $t1,$t2,-100",
                "Set less than immediate unsigned : If $t2 is less than  sign-extended 16-bit immediate using unsigned"
                        + " comparison, then set $t1 to 1 else set $t1 to 0",
                BasicInstructionFormat.I_FORMAT,
                "001011 sssss fffff tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    int first = RegisterFile.getValue(operands[1]);
                    // 16 bit immediate value in operands[2] is sign-extended
                    int second = operands[2] << 16 >> 16;
                    if (first >= 0 && second >= 0 || first < 0 && second < 0) {
                        RegisterFile.updateRegister(operands[0], (first < second) ? 1 : 0);
                    } else {
                        RegisterFile.updateRegister(operands[0], (first >= 0) ? 1 : 0);
                    }
                });
    }
}
