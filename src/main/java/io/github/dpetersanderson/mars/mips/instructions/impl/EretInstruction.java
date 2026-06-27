package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor0;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class EretInstruction extends BasicInstruction {
    public EretInstruction() {
        super(
                "eret",
                "Exception return : Set Program Counter to Coprocessor 0 EPC register value, set Coprocessor Status"
                        + " register bit 1 (exception level) to zero",
                BasicInstructionFormat.R_FORMAT,
                "010000 1 0000000000000000000 011000",
                statement -> {

                    // set EXL bit (bit 1) in Status register to 0 and set PC to EPC
                    Coprocessor0.updateRegister(
                            Coprocessor0.STATUS,
                            Binary.clearBit(Coprocessor0.getValue(Coprocessor0.STATUS), Coprocessor0.EXCEPTION_LEVEL));
                    RegisterFile.setProgramCounter(Coprocessor0.getValue(Coprocessor0.EPC));
                });
    }
}
