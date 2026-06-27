package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Lwc1Instruction extends BasicInstruction {
    public Lwc1Instruction() {
        super(
                "lwc1 $f1,-100($t2)",
                "Load word into Coprocessor 1 (FPU) : Set $f1 to 32-bit value from effective memory word address",
                BasicInstructionFormat.I_FORMAT,
                "110001 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Coprocessor1.updateRegister(
                                operands[0], Globals.memory.getWord(RegisterFile.getValue(operands[2]) + operands[1]));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
