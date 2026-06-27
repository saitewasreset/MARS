package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class CLtD2Instruction extends BasicInstruction {
    public CLtD2Instruction() {
        super(
                "c.lt.d 1,$f2,$f4",
                "Compare less than double precision : If $f2 is less than $f4 (double-precision), set Coprocessor 1"
                        + " condition flag specified by immediate to true else set it to false",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 ttttt sssss fff 00 111100",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[1] % 2 == 1 || operands[2] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    double op1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    double op2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[2] + 1), Coprocessor1.getValue(operands[2])));
                    if (op1 < op2) Coprocessor1.setConditionFlag(operands[0]);
                    else Coprocessor1.clearConditionFlag(operands[0]);
                });
    }
}
