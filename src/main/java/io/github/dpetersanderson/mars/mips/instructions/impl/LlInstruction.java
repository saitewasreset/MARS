package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class LlInstruction extends BasicInstruction {
    public LlInstruction() {
        super(
                "ll $t1,-100($t2)",
                "Load linked : Paired with Store Conditional (sc) to perform atomic read-modify-write.  Treated as"
                        + " equivalent to Load Word (lw) because MARS does not simulate multiple processors.",
                BasicInstructionFormat.I_FORMAT,
                "110000 ttttt fffff ssssssssssssssss",
                // The ll (load link) command is supposed to be the front end of an atomic
                // operation completed by sc (store conditional), with success or failure
                // of the store depending on whether the memory block containing the
                // loaded word is modified in the meantime by a different processor.
                // Since MARS, like SPIM simulates only a single processor, the store
                // conditional will always succeed so there is no need to do anything
                // special here.  In that case, ll is same as lw.  And sc does the same
                // thing as sw except in addition it writes 1 into the source register.
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
