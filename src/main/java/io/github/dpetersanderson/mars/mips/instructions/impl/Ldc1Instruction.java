package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class Ldc1Instruction extends BasicInstruction {
    public Ldc1Instruction() {
        super(
                "ldc1 $f2,-100($t2)",
                "Load double word Coprocessor 1 (FPU)) : Set $f2 to 64-bit value from effective memory"
                        + " doubleword address",
                BasicInstructionFormat.I_FORMAT,
                "110101 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1) {
                        throw new ProcessingException(statement, "first register must be even-numbered");
                    }
                    // IF statement added by DPS 13-July-2011.
                    if (!Globals.memory.doublewordAligned(RegisterFile.getValue(operands[2]) + operands[1])) {
                        throw new ProcessingException(
                                statement,
                                new AddressErrorException(
                                        "address not aligned on doubleword boundary ",
                                        Exceptions.ADDRESS_EXCEPTION_LOAD,
                                        RegisterFile.getValue(operands[2]) + operands[1]));
                    }

                    try {
                        Coprocessor1.updateRegister(
                                operands[0], Globals.memory.getWord(RegisterFile.getValue(operands[2]) + operands[1]));
                        Coprocessor1.updateRegister(
                                operands[0] + 1,
                                Globals.memory.getWord(RegisterFile.getValue(operands[2]) + operands[1] + 4));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
