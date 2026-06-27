package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class RoundWSInstruction extends BasicInstruction {
    public RoundWSInstruction() {
        super(
                "round.w.s $f0,$f1",
                "Round single precision to word : Set $f0 to 32-bit integer round of single-precision float in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 001100",
                statement -> {
                    // MIPS32 documentation (and IEEE 754) states that round
                    // rounds to the nearest but when
                    // both are equally near it rounds to the even one!  SPIM rounds -4.5, -5.5,
                    // 4.5 and 5.5 to (-4, -5, 5, 6).  Curiously, it rounds -5.1 to -4 and -5.6 to -5.
                    // Until MARS 3.5, I used Math.round, which rounds to nearest but when both are
                    // equal it rounds toward positive infinity.  With Release 3.5, I painstakingly
                    // carry out the MIPS and IEEE 754 standard.
                    int[] operands = statement.getOperands();
                    float floatValue = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    int below = 0, above = 0, round = Math.round(floatValue);
                    // According to MIPS32 spec, if any of these conditions is true, set
                    // Invalid Operation in the FCSR (Floating point Control/Status Register) and
                    // set result to be 2^31-1.  MARS does not implement this register (as of release 3.4.1).
                    // It also mentions the "Invalid Operation Enable bit" in FCSR, that, if set, results
                    // in immediate exception instead of default value.
                    if (Float.isNaN(floatValue)
                            || Float.isInfinite(floatValue)
                            || floatValue < (float) Integer.MIN_VALUE
                            || floatValue > (float) Integer.MAX_VALUE) {
                        round = Integer.MAX_VALUE;
                    } else {
                        Float floatObj = Float.valueOf(floatValue);
                        // If we are EXACTLY in the middle, then round to even!  To determine this,
                        // find next higher integer and next lower integer, then see if distances
                        // are exactly equal.
                        if (floatValue < 0.0F) {
                            above = floatObj.intValue(); // truncates
                            below = above - 1;
                        } else {
                            below = floatObj.intValue(); // truncates
                            above = below + 1;
                        }
                        if (floatValue - below == above - floatValue) { // exactly in the middle?
                            round = (above % 2 == 0) ? above : below;
                        }
                    }
                    Coprocessor1.updateRegister(operands[0], round);
                });
    }
}
