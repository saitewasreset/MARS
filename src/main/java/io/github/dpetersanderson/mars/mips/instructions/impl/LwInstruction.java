package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class LwInstruction extends BasicInstruction {
    public LwInstruction() {
        super(
                "lw $t1,-100($t2)",
                "Load word : Set $t1 to contents of effective memory word address",
                BasicInstructionFormat.I_FORMAT,
                "100011 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        RegisterFile.updateRegister(
                                operands[0], Globals.memory.getWord(RegisterFile.getValue(operands[2]) + operands[1]));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
