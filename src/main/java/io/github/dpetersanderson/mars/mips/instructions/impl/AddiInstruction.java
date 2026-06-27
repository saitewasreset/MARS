package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class AddiInstruction extends BasicInstruction {
    public AddiInstruction() {
        super(
                "addi $t1,$t2,-100",
                "Addition immediate with overflow : set $t1 to ($t2 plus signed 16-bit immediate)",
                BasicInstructionFormat.I_FORMAT,
                "001000 sssss fffff tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    int add1 = RegisterFile.getValue(operands[1]);
                    int add2 = operands[2] << 16 >> 16;
                    int sum = add1 + add2;
                    // overflow on A+B detected when A and B have same sign and A+B has other sign.
                    if ((add1 >= 0 && add2 >= 0 && sum < 0) || (add1 < 0 && add2 < 0 && sum >= 0)) {
                        throw new ProcessingException(
                                statement, "arithmetic overflow", Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                    }
                    RegisterFile.updateRegister(operands[0], sum);
                });
    }
}
