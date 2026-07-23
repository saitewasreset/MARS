package io.github.dpetersanderson.mars;

import static org.junit.jupiter.api.Assertions.*;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.impl.Teq2Instruction;
import io.github.dpetersanderson.mars.mips.instructions.impl.TeqInstruction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TeqInstructionTest {
    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @BeforeEach
    void resetRegisters() {
        RegisterFile.resetRegisters();
    }

    @Test
    void assemblerSupportsTeqWithAndWithoutCode(@TempDir Path tempDir) throws Exception {
        List<ProgramStatement> statements = assemble(tempDir, "teq.asm", ".text", "teq $2,$0", "teq $2,$0,7");

        assertEquals(2, statements.size());
        assertEquals(0x00400034, statements.get(0).getBinaryStatement());
        assertEquals(0x004001f4, statements.get(1).getBinaryStatement());
        assertInstanceOf(TeqInstruction.class, statements.get(0).getInstruction());
        assertInstanceOf(Teq2Instruction.class, statements.get(1).getInstruction());
        assertEquals(7, statements.get(1).getOperand(2));
    }

    @Test
    void binaryDecoderUsesShortFormOnlyWhenCodeIsZero() {
        ProgramStatement shortForm = new ProgramStatement(0x00400034, 0);
        ProgramStatement codeForm = new ProgramStatement(0x004001f4, 0);

        assertInstanceOf(TeqInstruction.class, shortForm.getInstruction());
        assertEquals(-1, shortForm.getOperand(2));
        assertInstanceOf(Teq2Instruction.class, codeForm.getInstruction());
        assertEquals(7, codeForm.getOperand(2));
    }

    @Test
    void bothFormsUseTheSameTrapCondition(@TempDir Path tempDir) throws Exception {
        List<ProgramStatement> statements =
                assemble(tempDir, "teq-simulation.asm", ".text", "teq $2,$0", "teq $2,$0,7");

        for (ProgramStatement statement : statements) {
            BasicInstruction instruction = (BasicInstruction) statement.getInstruction();
            assertThrows(
                    ProcessingException.class,
                    () -> instruction.getSimulationCode().simulate(statement));

            RegisterFile.updateRegister(2, 1);
            assertDoesNotThrow(() -> instruction.getSimulationCode().simulate(statement));
            RegisterFile.updateRegister(2, 0);
        }
    }

    private static List<ProgramStatement> assemble(Path tempDir, String filename, String... lines) throws Exception {
        Path source = tempDir.resolve(filename);
        Files.write(source, List.of(lines), StandardCharsets.UTF_8);

        MIPSprogram program = new MIPSprogram();
        ArrayList<String> filenames = new ArrayList<>();
        filenames.add(source.toString());
        ArrayList<MIPSprogram> programs = program.prepareFilesForAssembly(filenames, source.toString(), null);
        ErrorList warnings = program.assemble(programs, true, false);

        assertFalse(warnings.errorsOccurred());
        return new ArrayList<>(program.getMachineList());
    }
}
