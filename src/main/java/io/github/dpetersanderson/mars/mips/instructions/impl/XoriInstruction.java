package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class XoriInstruction extends BasicInstruction {
    public XoriInstruction() {
        super(
                "xori $t1,$t2,100",
                "Bitwise XOR immediate : Set $t1 to bitwise XOR of $t2 and zero-extended 16-bit immediate",
                BasicInstructionFormat.I_FORMAT,
                "001110 sssss fffff tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    // ANDing with 0x0000FFFF zero-extends the immediate (high 16 bits always 0).
                    RegisterFile.updateRegister(
                            operands[0], RegisterFile.getValue(operands[1]) ^ (operands[2] & 0x0000FFFF));
                });
    }
}
