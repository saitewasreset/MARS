package io.github.dpetersanderson.mars;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

class TestUtils {

    static MIPSprogram assembleProgram(Path tempDir, String filename, String... lines) throws Exception {
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

    static MIPSprogram assembleProgram(String... lines) throws Exception {
        Path tempDir = Files.createTempDirectory("mars-test-");
        try {
            return assembleProgram(tempDir, "test.asm", lines);
        } finally {
            deleteRecursively(tempDir);
        }
    }

    private static Path writeProgram(Path tempDir, String filename, String... lines) throws IOException {
        Path source = tempDir.resolve(filename);
        Files.write(source, Arrays.asList(lines), StandardCharsets.UTF_8);
        return source;
    }

    private static void deleteRecursively(Path path) {
        try {
            if (Files.isDirectory(path)) {
                try (var entries = Files.newDirectoryStream(path)) {
                    for (Path entry : entries) {
                        deleteRecursively(entry);
                    }
                }
            }
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
