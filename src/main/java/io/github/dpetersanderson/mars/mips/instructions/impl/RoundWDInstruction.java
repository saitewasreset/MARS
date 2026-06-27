package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class RoundWDInstruction extends BasicInstruction {
    public RoundWDInstruction() {
        super(
                "round.w.d $f1,$f2",
                "Round double precision to word : Set $f1 to 32-bit integer round of double-precision float in $f2",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 00000 sssss fffff 001100",
                statement -> {
                    // See comments in round.w.s above, concerning MIPS and IEEE
                    // 754 standard.
                    // Until MARS 3.5, I used Math.round, which rounds to nearest but when both are
                    // equal it rounds toward positive infinity.  With Release 3.5, I painstakingly
                    // carry out the MIPS and IEEE 754 standard (round to nearest/even).
                    int[] operands = statement.getOperands();
                    if (operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                    }
                    double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    int below = 0, above = 0;
                    int round = (int) Math.round(doubleValue);
                    // See comments in round.w.s above concerning FSCR...
                    if (Double.isNaN(doubleValue)
                            || Double.isInfinite(doubleValue)
                            || doubleValue < (double) Integer.MIN_VALUE
                            || doubleValue > (double) Integer.MAX_VALUE) {
                        round = Integer.MAX_VALUE;
                    } else {
                        Double doubleObj = Double.valueOf(doubleValue);
                        // If we are EXACTLY in the middle, then round to even!  To determine this,
                        // find next higher integer and next lower integer, then see if distances
                        // are exactly equal.
                        if (doubleValue < 0.0) {
                            above = doubleObj.intValue(); // truncates
                            below = above - 1;
                        } else {
                            below = doubleObj.intValue(); // truncates
                            above = below + 1;
                        }
                        if (doubleValue - below == above - doubleValue) { // exactly in the middle?
                            round = (above % 2 == 0) ? above : below;
                        }
                    }
                    Coprocessor1.updateRegister(operands[0], round);
                });
    }
}
