package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SwInstruction extends BasicInstruction {
    public SwInstruction() {
        super(
                "sw $t1,-100($t2)",
                "Store word : Store contents of $t1 into effective memory word address",
                BasicInstructionFormat.I_FORMAT,
                "101011 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + operands[1], RegisterFile.getValue(operands[0]));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
