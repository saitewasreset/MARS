package io.github.dpetersanderson.mars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemIOTest {
    private InputStream originalInput;

    @BeforeEach
    void saveStandardInput() {
        originalInput = System.in;
    }

    @AfterEach
    void restoreStandardInput() throws ReflectiveOperationException {
        System.setIn(originalInput);
        resetInputReader();
    }

    @Test
    void readCharReadsExactlyOneCharacterPerCall() throws ReflectiveOperationException {
        setStandardInput("AB");

        assertEquals('A', SystemIO.readChar(12));
        assertEquals('B', SystemIO.readChar(12));
    }

    @Test
    void readCharReturnsEofAtEndOfStream() throws ReflectiveOperationException {
        setStandardInput("");

        assertEquals(-1, SystemIO.readChar(12));
    }

    private static void setStandardInput(String input) throws ReflectiveOperationException {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        resetInputReader();
    }

    private static void resetInputReader() throws ReflectiveOperationException {
        Field inputReader = SystemIO.class.getDeclaredField("inputReader");
        inputReader.setAccessible(true);
        inputReader.set(null, null);
    }
}
