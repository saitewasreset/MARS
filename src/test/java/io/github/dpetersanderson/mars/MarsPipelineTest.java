package io.github.dpetersanderson.mars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dpetersanderson.mars.assembler.Token;
import io.github.dpetersanderson.mars.assembler.TokenList;
import io.github.dpetersanderson.mars.assembler.TokenTypes;
import io.github.dpetersanderson.mars.assembler.Tokenizer;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor0;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.MemoryConfigurations;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.util.SystemIO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarsPipelineTest {
    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @BeforeEach
    void resetMarsState() {
        MemoryConfigurations.setCurrentConfiguration(MemoryConfigurations.getDefaultConfiguration());
        Globals.memory.clear();
        Globals.symbolTable.clear();
        Globals.program = null;
        Globals.exitCode = 0;
        Globals.debug = false;
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.DELAYED_BRANCHING_ENABLED, false);
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.SELF_MODIFYING_CODE_ENABLED, false);
        Globals.getSettings().setBooleanSettingNonPersistent(Settings.START_AT_MAIN, false);
        RegisterFile.resetRegisters();
        Coprocessor0.resetRegisters();
        Coprocessor1.resetRegisters();
        SystemIO.resetFiles();
    }

    @Test
    void tokenizerRecognizesInstructionLabelsRegistersIntegersAndComments() {
        TokenList tokens = new Tokenizer().tokenizeLine(1, "main: addi $t0, $zero, 5 # comment");

        assertEquals(7, tokens.size());
        assertToken(tokens.get(0), TokenTypes.IDENTIFIER, "main");
        assertToken(tokens.get(1), TokenTypes.COLON, ":");
        assertToken(tokens.get(2), TokenTypes.OPERATOR, "addi");
        assertToken(tokens.get(3), TokenTypes.REGISTER_NAME, "$t0");
        assertToken(tokens.get(4), TokenTypes.REGISTER_NAME, "$zero");
        assertTrue(isInteger(tokens.get(5)), "expected an integer token for immediate value 5");
        assertEquals("5", tokens.get(5).getValue());
        assertToken(tokens.get(6), TokenTypes.COMMENT, "# comment");
    }

    @Test
    void tokenizerKeepsCommaInsideQuotedString() {
        TokenList tokens = new Tokenizer().tokenizeLine(1, ".asciiz \"hi, mips\"");

        assertEquals(2, tokens.size());
        assertToken(tokens.get(0), TokenTypes.DIRECTIVE, ".asciiz");
        assertToken(tokens.get(1), TokenTypes.QUOTED_STRING, "\"hi, mips\"");
    }

    @Test
    void tokenizerRecognizesSupportedGnuCompatibilityDirectives() {
        Tokenizer tokenizer = new Tokenizer();

        TokenList moduleTokens = tokenizer.tokenizeLine(1, ".module\tarch=4kc");
        assertEquals(4, moduleTokens.size());
        assertToken(moduleTokens.get(0), TokenTypes.DIRECTIVE, ".module");
        assertToken(moduleTokens.get(1), TokenTypes.IDENTIFIER, "arch");
        assertToken(moduleTokens.get(2), TokenTypes.EQUALS, "=");
        assertToken(moduleTokens.get(3), TokenTypes.DIRECTIVE_VALUE, "4kc");
        assertEquals(1, moduleTokens.get(0).getStartPos());
        assertEquals(9, moduleTokens.get(1).getStartPos());
        assertEquals(13, moduleTokens.get(2).getStartPos());
        assertEquals(14, moduleTokens.get(3).getStartPos());

        TokenList typeTokens = tokenizer.tokenizeLine(2, ".type\tget_char, @function");
        assertEquals(4, typeTokens.size());
        assertToken(typeTokens.get(0), TokenTypes.DIRECTIVE, ".type");
        assertToken(typeTokens.get(1), TokenTypes.IDENTIFIER, "get_char");
        assertToken(typeTokens.get(2), TokenTypes.AT, "@");
        assertToken(typeTokens.get(3), TokenTypes.IDENTIFIER, "function");
        assertEquals(1, typeTokens.get(0).getStartPos());
        assertEquals(7, typeTokens.get(1).getStartPos());
        assertEquals(17, typeTokens.get(2).getStartPos());
        assertEquals(18, typeTokens.get(3).getStartPos());
        assertFalse(tokenizer.getErrors().errorsOccurred());
    }

    @Test
    void assemblerIgnoresDirectiveArguments(@TempDir Path tempDir) throws Exception {
        Path source = writeProgram(
                tempDir,
                "gnu-directives.asm",
                ".module\tarch=5kc extra!",
                ".type\tget_char, @object unexpected!",
                ".set unsupported-option!",
                ".unknown arch=99x!",
                ".text",
                "get_char:",
                "addi $v0, $zero, 12");
        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(source.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, source.toString(), null);

        ErrorList warnings = program.assemble(programs, true, false);

        assertFalse(warnings.errorsOccurred());
        assertEquals(4, warnings.warningCount());
        String warningReport = warnings.generateWarningReport();
        assertTrue(warningReport.contains("ignores the .module directive"));
        assertTrue(warningReport.contains("ignores the .type directive"));
        assertTrue(warningReport.contains("ignores the .set directive"));
        assertTrue(warningReport.contains("does not recognize the .unknown directive"));
        List<ProgramStatement> statements = machineStatements(program);
        assertEquals(1, statements.size());
        assertEquals(0x2002000c, statements.get(0).getBinaryStatement());
    }

    @Test
    void assemblerRejectsDirectiveTokensAsInstructionOperands(@TempDir Path tempDir) throws Exception {
        assertDirectiveAssemblyFails(tempDir, "directive-token-as-operand.asm", ".text", "addi $t0, $zero, @function");
    }

    @Test
    void assemblerEmitsExpectedMachineCode(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir, "machine-code.asm", ".text", "addi $t0, $zero, 5", "addi $t1, $zero, 7", "add $t2, $t0, $t1");

        List<ProgramStatement> machineStatements = machineStatements(program);

        assertEquals(3, machineStatements.size());
        assertEquals(0x20080005, machineStatements.get(0).getBinaryStatement());
        assertEquals(0x20090007, machineStatements.get(1).getBinaryStatement());
        assertEquals(0x01095020, machineStatements.get(2).getBinaryStatement());
    }

    @Test
    void assemblerExpandsAndSimulatesSltImmediate(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir,
                "slt-immediate.asm",
                ".text",
                "slt $t0, $t1, 32767",
                "slt $t2, $t1, -32768",
                "slt $t3, $t1, 32768",
                "slt $t4, $t1, -32769",
                "slt $t5, $t2, $t0",
                "addi $v0, $zero, 10",
                "syscall");

        List<ProgramStatement> statements = machineStatements(program);
        assertEquals(11, statements.size());
        assertEquals(0x29287fff, statements.get(0).getBinaryStatement());
        assertEquals(0x292a8000, statements.get(1).getBinaryStatement());
        assertEquals(0x3c010000, statements.get(2).getBinaryStatement());
        assertEquals(0x34218000, statements.get(3).getBinaryStatement());
        assertEquals(0x0121582a, statements.get(4).getBinaryStatement());
        assertEquals(0x3c01ffff, statements.get(5).getBinaryStatement());
        assertEquals(0x34217fff, statements.get(6).getBinaryStatement());
        assertEquals(0x0121602a, statements.get(7).getBinaryStatement());
        assertEquals(0x0148682a, statements.get(8).getBinaryStatement());

        RegisterFile.initializeProgramCounter(false);
        assertTrue(program.simulate(20));
        assertEquals(1, RegisterFile.getValue(8));
        assertEquals(0, RegisterFile.getValue(10));
        assertEquals(1, RegisterFile.getValue(11));
        assertEquals(0, RegisterFile.getValue(12));
        assertEquals(1, RegisterFile.getValue(13));
    }

    @Test
    void assemblerResolvesHighAndLowRelocationsForForwardSymbol(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir,
                "relocation.asm",
                ".text",
                "lui $t0, %hi(pow2 + 4)",
                "addiu $t0, $t0, %lo(pow2 + 4)",
                ".data",
                ".space 32764",
                "pow2:",
                ".word 0");

        List<ProgramStatement> statements = machineStatements(program);
        assertEquals(2, statements.size());
        assertEquals(0x3c081002, statements.get(0).getBinaryStatement());
        assertEquals(0x25088000, statements.get(1).getBinaryStatement());
        assertEquals(
                "%hi(pow2 + 4)", statements.get(0).getOriginalTokenList().get(2).getValue());
        assertEquals(
                TokenTypes.RELOCATION_HIGH,
                statements.get(0).getOriginalTokenList().get(2).getType());

        RegisterFile.initializeProgramCounter(false);
        assertFalse(program.simulate(2));
        assertEquals(0x10018000, RegisterFile.getValue(8));
    }

    @Test
    void assemblerSupportsLocalDollarSymbolAndNegativeAddend(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir,
                "local-relocation.asm",
                ".text",
                "$L30:",
                "lui $t0, %hi($L30-4)",
                "addiu $t0, $t0, %lo($L30-4)");

        List<ProgramStatement> statements = machineStatements(program);
        assertEquals(0x3c080040, statements.get(0).getBinaryStatement());
        assertEquals(0x2508fffc, statements.get(1).getBinaryStatement());
    }

    @Test
    void assemblerResolvesGlobalSymbolFromAnotherFile(@TempDir Path tempDir) throws Exception {
        Path mainSource = writeProgram(
                tempDir, "global-relocation-main.asm", ".text", "lui $t0, %hi(shared)", "addiu $t0, $t0, %lo(shared)");
        Path symbolSource =
                writeProgram(tempDir, "global-relocation-symbol.asm", ".data", ".globl shared", "shared:", ".word 0");
        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(mainSource.toString());
        filenames.add(symbolSource.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, mainSource.toString(), null);

        ErrorList warnings = program.assemble(programs, true, false);

        assertFalse(warnings.errorsOccurred());
        List<ProgramStatement> statements = machineStatements(program);
        assertEquals(0x3c081001, statements.get(0).getBinaryStatement());
        assertEquals(0x25080000, statements.get(1).getBinaryStatement());
    }

    @Test
    void assemblerRejectsUndefinedRelocationSymbolAndWrongOperandPosition(@TempDir Path tempDir) throws Exception {
        assertAssemblyFailsWith(
                tempDir, "undefined-relocation.asm", "Symbol \"missing\" not found", ".text", "lui $t0, %hi(missing)");
        assertAssemblyFailsWith(
                tempDir,
                "wrong-relocation-operand.asm",
                "operand is of incorrect type",
                ".text",
                "sll $t0, $t1, %lo(target)",
                "target:",
                "nop");
    }

    @Test
    void simulatorRunsAssembledMachineCodeAndUpdatesRegisters(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir,
                "simulate.asm",
                ".text",
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 7",
                "add $t2, $t0, $t1",
                "addi $v0, $zero, 10",
                "syscall");

        RegisterFile.initializeProgramCounter(false);

        assertTrue(program.simulate(20));
        assertEquals(5, RegisterFile.getValue(8));
        assertEquals(7, RegisterFile.getValue(9));
        assertEquals(12, RegisterFile.getValue(10));
        assertEquals(10, RegisterFile.getValue(2));
    }

    @Test
    void completeApiFlowReadsAssemblesSimulatesFileAndReportsFinalRegisters(@TempDir Path tempDir) throws Exception {
        MIPSprogram program = assembleProgram(
                tempDir,
                "full-flow.asm",
                ".text",
                ".globl main",
                "addi $t2, $zero, 1",
                "main:",
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 7",
                "add $t2, $t0, $t1",
                "addi $v0, $zero, 10",
                "syscall");

        RegisterFile.initializeProgramCounter(true);

        assertTrue(program.simulate(20));
        assertEquals(5, RegisterFile.getValue(8));
        assertEquals(7, RegisterFile.getValue(9));
        assertEquals(12, RegisterFile.getValue(10));
        assertEquals(10, RegisterFile.getValue(2));
    }

    @Test
    void commandLineFlowRunsInForkedJvmAndPrintsRequestedRegister(@TempDir Path tempDir) throws Exception {
        Path source = writeProgram(
                tempDir,
                "cli-flow.asm",
                ".text",
                "addi $t0, $zero, 5",
                "addi $t1, $zero, 7",
                "add $t2, $t0, $t1",
                "addi $v0, $zero, 10",
                "syscall");
        ProcessResult result =
                runMarsCommand("--no-copyright", "--register", "t2", "--max-steps", "20", source.toString());

        assertEquals(0, result.exitCode);
        assertTrue(result.output.contains("$t2"));
        assertTrue(result.output.contains("0x0000000c"));
        assertFalse(result.output.contains("Processing terminated due to errors."));
    }

    private static MIPSprogram assembleProgram(Path tempDir, String filename, String... lines) throws Exception {
        Path source = writeProgram(tempDir, filename, lines);
        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(source.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, source.toString(), null);
        ErrorList warnings = program.assemble(programs, true, false);
        assertNotNull(warnings);
        assertFalse(warnings.errorsOccurred());
        return program;
    }

    private static void assertDirectiveAssemblyFails(Path tempDir, String filename, String... lines) throws Exception {
        Path source = writeProgram(tempDir, filename, lines);
        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(source.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, source.toString(), null);

        ProcessingException exception = assertThrows(ProcessingException.class, () -> program.assemble(programs, true));
        assertTrue(exception.errors().errorsOccurred());
    }

    private static void assertAssemblyFailsWith(Path tempDir, String filename, String expectedMessage, String... lines)
            throws Exception {
        Path source = writeProgram(tempDir, filename, lines);
        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(source.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, source.toString(), null);

        ProcessingException exception = assertThrows(ProcessingException.class, () -> program.assemble(programs, true));
        assertTrue(exception.errors().generateErrorReport().contains(expectedMessage));
    }

    private static Path writeProgram(Path tempDir, String filename, String... lines) throws IOException {
        Path source = tempDir.resolve(filename);
        Files.write(source, Arrays.asList(lines), StandardCharsets.UTF_8);
        return source;
    }

    private static List<ProgramStatement> machineStatements(MIPSprogram program) {
        List<ProgramStatement> statements = new ArrayList<>();
        for (Object statement : program.getMachineList()) {
            statements.add((ProgramStatement) statement);
        }
        return statements;
    }

    private static ProcessResult runMarsCommand(String... args) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add("io.github.dpetersanderson.mars.Mars");
        command.addAll(Arrays.asList(args));

        Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        boolean finished = process.waitFor(10, TimeUnit.SECONDS);
        assertTrue(finished, "forked MARS command did not finish within timeout");
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        return new ProcessResult(process.exitValue(), output);
    }

    private static void assertToken(Token token, TokenTypes type, String value) {
        assertEquals(type, token.getType());
        assertEquals(value, token.getValue());
    }

    private static boolean isInteger(Token token) {
        return token.getType() == TokenTypes.INTEGER_5
                || token.getType() == TokenTypes.INTEGER_16
                || token.getType() == TokenTypes.INTEGER_16U
                || token.getType() == TokenTypes.INTEGER_32;
    }

    private static final class ProcessResult {
        private final int exitCode;
        private final String output;

        private ProcessResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}
