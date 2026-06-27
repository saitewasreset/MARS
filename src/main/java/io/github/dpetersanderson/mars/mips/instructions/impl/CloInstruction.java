package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class CloInstruction extends BasicInstruction {
    public CloInstruction() {
        super(
                "clo $t1,$t2",
                "Count number of leading ones : Set $t1 to the count of leading one bits in $t2 starting at most"
                        + " significant bit position",
                BasicInstructionFormat.R_FORMAT,
                // MIPS32 requires rd (first) operand to appear twice in machine code.
                // It has to be same as rt (third) operand in machine code, but the
                // source statement does not have or permit third operand.
                // In the machine code, rd and rt are adjacent, but my mask
                // substitution cannot handle adjacent placement of the same source
                // operand (e.g. "... sssss fffff fffff ...") because it would interpret
                // the mask to be the total length of both (10 bits).  I could code it
                // to have 3 operands then define a pseudo-instruction of two operands
                // to translate into this, but then both would show up in instruction set
                // list and I don't want that.  So I will use the convention of Computer
                // Organization and Design 3rd Edition, Appendix A, and code the rt bits
                // as 0's.  The generated code does not match SPIM and would not run
                // on a real MIPS machine but since I am providing no means of storing
                // the binary code that is not really an issue.
                "011100 sssss 00000 fffff 00000 100001",
                statement -> {
                    int[] operands = statement.getOperands();
                    int value = RegisterFile.getValue(operands[1]);
                    int leadingOnes = 0;
                    int bitPosition = 31;
                    while (Binary.bitValue(value, bitPosition) == 1 && bitPosition >= 0) {
                        leadingOnes++;
                        bitPosition--;
                    }
                    RegisterFile.updateRegister(operands[0], leadingOnes);
                });
    }
}
