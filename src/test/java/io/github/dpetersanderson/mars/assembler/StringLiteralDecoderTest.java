package io.github.dpetersanderson.mars.assembler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StringLiteralDecoderTest {
    @Test
    void decodesStandardOctalAndHexadecimalEscapes() throws Exception {
        assertArrayEquals(new byte[] {'A', '\n', 'B', (byte) 0xff}, StringLiteralDecoder.decode("\"A\\012B\\xff\""));
        assertArrayEquals(
                new byte[] {'\n', '\t', '\r', '\b', '\f', '\\', '\'', '"', 0},
                StringLiteralDecoder.decode("\"\\n\\t\\r\\b\\f\\\\\\'\\\"\\0\""));
        assertArrayEquals(new byte[] {'A', 'A'}, StringLiteralDecoder.decode("\"A\\101\"")); // octal 101 = 65
    }

    @Test
    void truncatesOctalEscapesAtThreeDigits() throws Exception {
        assertArrayEquals(new byte[] {'a', '\n', '1', '2', 'b'}, StringLiteralDecoder.decode("\"a\\01212b\""));
        assertArrayEquals(new byte[] {'c', 32, '0', 'd'}, StringLiteralDecoder.decode("\"c\\0400d\""));
        assertArrayEquals(new byte[] {'A', 1, '8'}, StringLiteralDecoder.decode("\"A\\018\""));
    }

    @Test
    void readsMaximalHexadecimalDigits() throws Exception {
        assertArrayEquals(new byte[] {'A', (byte) 0x12}, StringLiteralDecoder.decode("\"A\\x12\""));
        assertDecodeError("\"\\x100\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
        assertDecodeError("\"\\xffffffff\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
        assertDecodeError("\"\\xgg\"", StringLiteralDecoder.Reason.INVALID_NUMERIC_ESCAPE, 1);
    }

    @Test
    void rejectsOutOfRangeOctalEscapes() {
        assertDecodeError("\"\\400\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
        assertDecodeError("\"\\777\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
    }

    @Test
    void rejectsMalformedEscapes() {
        assertDecodeError("\"\\x\"", StringLiteralDecoder.Reason.INVALID_NUMERIC_ESCAPE, 1);
        assertDecodeError("\"\\q\"", StringLiteralDecoder.Reason.UNKNOWN_ESCAPE, 1);
        assertDecodeError("\"abc\\\"", StringLiteralDecoder.Reason.INCOMPLETE_ESCAPE, 4);
    }

    private static void assertDecodeError(String literal, StringLiteralDecoder.Reason reason, int offset) {
        StringLiteralDecoder.DecodeException exception =
                assertThrows(StringLiteralDecoder.DecodeException.class, () -> StringLiteralDecoder.decode(literal));
        assertEquals(reason, exception.getReason());
        assertEquals(offset, exception.getOffset());
    }
}
