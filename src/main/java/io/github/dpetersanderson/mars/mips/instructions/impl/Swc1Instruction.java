package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Swc1Instruction extends BasicInstruction {
    public Swc1Instruction() {
        super(
                "swc1 $f1,-100($t2)",
                "Store word from Coprocesor 1 (FPU) : Store 32 bit value in $f1 to effective memory word address",
                BasicInstructionFormat.I_FORMAT,
                "111001 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + operands[1], Coprocessor1.getValue(operands[0]));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
