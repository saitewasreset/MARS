package io.github.dpetersanderson.mars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarsCommandLineTest {
    private PrintStream originalOut;
    private PrintStream originalErr;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @BeforeEach
    void captureOutput() {
        originalOut = System.out;
        originalErr = System.err;
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void parsesModernCommandLineWithProgramArguments(@TempDir Path tempDir) throws Exception {
        Path program = Files.createFile(tempDir.resolve("program.asm"));

        MarsCommandLine.ParseResult result = MarsCommandLine.parse(new String[] {
            "--no-copyright",
            "--max-steps",
            "0x10",
            "--register",
            "t0",
            "--memory",
            "0x10010000-0x10010010",
            program.toString(),
            "--",
            "arg1",
            "-x"
        });

        assertTrue(result.shouldRun());
        MarsCommandLine.ParsedCommand command = result.command();
        assertTrue(command.simulate);
        assertEquals(16, command.maxSteps);
        assertEquals("$t0", command.registers.get(0));
        assertEquals("0x10010000", command.memoryRanges.get(0)[0]);
        assertEquals("0x10010010", command.memoryRanges.get(0)[1]);
        assertEquals(program.toString(), command.filenames.get(0));
        assertEquals("arg1", command.programArguments.get(0));
        assertEquals("-x", command.programArguments.get(1));
    }

    @Test
    void parsesRepeatedDumpOptions(@TempDir Path tempDir) throws Exception {
        Path program = Files.createFile(tempDir.resolve("program.asm"));

        MarsCommandLine.ParseResult result = MarsCommandLine.parse(new String[] {
            "--no-copyright",
            "--assemble-only",
            "--dump",
            ".text",
            "HexText",
            "text.txt",
            "--dump",
            ".data",
            "Binary",
            "data.bin",
            program.toString()
        });

        MarsCommandLine.ParsedCommand command = result.command();
        assertFalse(command.simulate);
        assertEquals(2, command.dumpTriples.size());
        assertEquals(".text", command.dumpTriples.get(0)[0]);
        assertEquals("HexText", command.dumpTriples.get(0)[1]);
        assertEquals("text.txt", command.dumpTriples.get(0)[2]);
        assertEquals(".data", command.dumpTriples.get(1)[0]);
    }

    @Test
    void routesMessagesToStderrBeforeValidation() {
        MarsCommandLine.ParseResult result = MarsCommandLine.parse(
                new String[] {"--messages-to-stderr", "--no-copyright", "--memory-configuration", "Missing"});

        assertFalse(result.shouldRun());
        assertSame(System.err, result.output());
        assertTrue(errContent.toString().contains("Invalid memory configuration: Missing"));
        assertEquals("", outContent.toString());
    }

    @Test
    void rejectsOldBareOptions() {
        MarsCommandLine.ParseResult result = MarsCommandLine.parse(new String[] {"--no-copyright", "a"});

        assertFalse(result.shouldRun());
        assertEquals(1, result.exitCode());
        assertTrue(outContent.toString().contains("Input file does not exist: a"));
    }

    @Test
    void rejectsInvalidRegister() {
        MarsCommandLine.ParseResult result =
                MarsCommandLine.parse(new String[] {"--no-copyright", "--register", "not_a_register"});

        assertFalse(result.shouldRun());
        assertEquals(1, result.exitCode());
        assertTrue(outContent.toString().contains("Invalid register name: not_a_register"));
    }

    @Test
    void rejectsInvalidMemoryRange() {
        MarsCommandLine.ParseResult result =
                MarsCommandLine.parse(new String[] {"--no-copyright", "--memory", "0x10010002-0x10010010"});

        assertFalse(result.shouldRun());
        assertEquals(1, result.exitCode());
        assertTrue(outContent.toString().contains("Invalid/unaligned address or invalid range"));
    }

    @Test
    void rejectsInvalidDisplayFormat() {
        MarsCommandLine.ParseResult result =
                MarsCommandLine.parse(new String[] {"--no-copyright", "--display-format", "octal"});

        assertFalse(result.shouldRun());
        assertEquals(1, result.exitCode());
        assertTrue(outContent.toString().contains("Expected one of: hex, dec, ascii"));
    }
}
