package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.ProgramStatement;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.mips.instructions.SyscallLoader;
import io.github.dpetersanderson.mars.mips.instructions.syscalls.Syscall;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class SyscallInstruction extends BasicInstruction {
    private static final SyscallLoader SYSCALL_LOADER = new SyscallLoader();

    public SyscallInstruction() {
        super(
                "syscall",
                "Issue a system call : Execute the system call specified by value in $v0",
                BasicInstructionFormat.R_FORMAT,
                "000000 ccccc ccccc ccccc ccccc 001100",
                statement -> {
                    findAndSimulateSyscall(RegisterFile.getValue(2), statement);
                });
    }

    private static void findAndSimulateSyscall(int number, ProgramStatement statement) throws ProcessingException {
        Syscall service = SYSCALL_LOADER.findSyscall(number);
        if (service != null) {
            service.simulate(statement);
            return;
        }
        throw new ProcessingException(
                statement, "invalid or unimplemented syscall service: " + number + " ", Exceptions.SYSCALL_EXCEPTION);
    }
}
