package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class ScInstruction extends BasicInstruction {
    public ScInstruction() {
        super(
                "sc $t1,-100($t2)",
                "Store conditional : Paired with Load Linked (ll) to perform atomic read-modify-write.  Stores $t1"
                        + " value into effective address, then sets $t1 to 1 for success.  Always succeeds because MARS"
                        + " does not simulate multiple processors.",
                BasicInstructionFormat.I_FORMAT,
                "111000 ttttt fffff ssssssssssssssss",
                // See comments with "ll" instruction above.  "sc" is implemented
                // like "sw", except that 1 is placed in the source register.
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + operands[1], RegisterFile.getValue(operands[0]));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                    RegisterFile.updateRegister(operands[0], 1); // always succeeds
                });
    }
}
