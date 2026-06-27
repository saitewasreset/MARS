package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class Sdc1Instruction extends BasicInstruction {
    public Sdc1Instruction() {
        super(
                "sdc1 $f2,-100($t2)",
                "Store double word from Coprocessor 1 (FPU)) : Store 64 bit value in $f2 to effective memory"
                        + " doubleword address",
                BasicInstructionFormat.I_FORMAT,
                "111101 ttttt fffff ssssssssssssssss",
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
                                        Exceptions.ADDRESS_EXCEPTION_STORE,
                                        RegisterFile.getValue(operands[2]) + operands[1]));
                    }
                    try {
                        Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + operands[1], Coprocessor1.getValue(operands[0]));
                        Globals.memory.setWord(
                                RegisterFile.getValue(operands[2]) + operands[1] + 4,
                                Coprocessor1.getValue(operands[0] + 1));
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
