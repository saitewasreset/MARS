package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class ClzInstruction extends BasicInstruction {
    public ClzInstruction() {
        super(
                "clz $t1,$t2",
                "Count number of leading zeroes : Set $t1 to the count of leading zero bits in $t2 starting at most"
                        + " significant bit positio",
                BasicInstructionFormat.R_FORMAT,
                // See comments for "clo" instruction above.  They apply here too.
                "011100 sssss 00000 fffff 00000 100000",
                statement -> {
                    int[] operands = statement.getOperands();
                    int value = RegisterFile.getValue(operands[1]);
                    int leadingZeros = 0;
                    int bitPosition = 31;
                    while (Binary.bitValue(value, bitPosition) == 0 && bitPosition >= 0) {
                        leadingZeros++;
                        bitPosition--;
                    }
                    RegisterFile.updateRegister(operands[0], leadingZeros);
                });
    }
}
