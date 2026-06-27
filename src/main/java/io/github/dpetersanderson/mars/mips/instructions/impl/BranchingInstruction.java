package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.mips.instructions.Instruction;
import io.github.dpetersanderson.mars.mips.instructions.SimulationCode;
import io.github.dpetersanderson.mars.simulator.DelayedBranch;

abstract class BranchingInstruction extends BasicInstruction {
    protected BranchingInstruction(
            String example,
            String description,
            BasicInstructionFormat instrFormat,
            String operMask,
            SimulationCode simCode) {
        super(example, description, instrFormat, operMask, simCode);
    }

    protected static void processBranch(int displacement) {
        if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(RegisterFile.getProgramCounter() + (displacement << 2));
        } else {
            RegisterFile.setProgramCounter(RegisterFile.getProgramCounter() + (displacement << 2));
        }
    }

    protected static void processJump(int targetAddress) {
        if (Globals.getSettings().getDelayedBranchingEnabled()) {
            DelayedBranch.register(targetAddress);
        } else {
            RegisterFile.setProgramCounter(targetAddress);
        }
    }

    protected static void processReturnAddress(int register) {
        RegisterFile.updateRegister(
                register,
                RegisterFile.getProgramCounter()
                        + ((Globals.getSettings().getDelayedBranchingEnabled()) ? Instruction.INSTRUCTION_LENGTH : 0));
    }
}
