package io.github.dpetersanderson.mars.mips.instructions.syscalls;

import java.util.List;

public final class SyscallRegistry {
    private static final List<Syscall> syscalls = List.of(
            new SyscallClearScreen(),
            new SyscallClose(),
            new SyscallConfirmDialog(),
            new SyscallExit(),
            new SyscallExit2(),
            new SyscallInputDialogDouble(),
            new SyscallInputDialogFloat(),
            new SyscallInputDialogInt(),
            new SyscallInputDialogString(),
            new SyscallMessageDialog(),
            new SyscallMessageDialogDouble(),
            new SyscallMessageDialogFloat(),
            new SyscallMessageDialogInt(),
            new SyscallMessageDialogString(),
            new SyscallMidiOut(),
            new SyscallMidiOutSync(),
            new SyscallOpen(),
            new SyscallPrintChar(),
            new SyscallPrintDouble(),
            new SyscallPrintFloat(),
            new SyscallPrintInt(),
            new SyscallPrintIntBinary(),
            new SyscallPrintIntHex(),
            new SyscallPrintIntUnsigned(),
            new SyscallPrintString(),
            new SyscallRandDouble(),
            new SyscallRandFloat(),
            new SyscallRandInt(),
            new SyscallRandIntRange(),
            new SyscallRandSeed(),
            new SyscallRead(),
            new SyscallReadChar(),
            new SyscallReadDouble(),
            new SyscallReadFloat(),
            new SyscallReadInt(),
            new SyscallReadString(),
            new SyscallSbrk(),
            new SyscallSleep(),
            new SyscallTime(),
            new SyscallWrite());

    private SyscallRegistry() {}

    public static List<Syscall> getSyscalls() {
        return syscalls;
    }
}
