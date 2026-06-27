package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class CEqDInstruction extends BasicInstruction {
    public CEqDInstruction() {
        super(
                "c.eq.d $f2,$f4",
                "Compare equal double precision : If $f2 is equal to $f4 (double-precision), set Coprocessor 1"
                        + " condition flag 0 true else set it false",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 sssss fffff 00000 110010",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[0] + 1), Coprocessor1.getValue(operands[0])));
                    double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    if (op1 == op2) Coprocessor1.setConditionFlag(0);
                    else Coprocessor1.clearConditionFlag(0);
                });
    }
}
