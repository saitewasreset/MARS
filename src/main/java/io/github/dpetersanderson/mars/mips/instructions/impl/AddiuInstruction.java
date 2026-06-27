package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class AddiuInstruction extends BasicInstruction {
    public AddiuInstruction() {
        super(
                "addiu $t1,$t2,-100",
                "Addition immediate unsigned without overflow : set $t1 to ($t2 plus signed 16-bit immediate), no"
                        + " overflow",
                BasicInstructionFormat.I_FORMAT,
                "001001 sssss fffff tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) + (operands[2] << 16 >> 16));
                });
    }
}
