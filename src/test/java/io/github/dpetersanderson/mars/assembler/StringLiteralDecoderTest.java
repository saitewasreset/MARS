package io.github.dpetersanderson.mars.assembler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StringLiteralDecoderTest {
    @Test
    void decodesStandardOctalAndHexadecimalEscapes() throws Exception {
        assertArrayEquals(new byte[] {'A', '\n', 'B', (byte) 0xff}, StringLiteralDecoder.decode("\"A\\012B\\0xff\""));
        assertArrayEquals(
                new byte[] {'\n', '\t', '\r', '\b', '\f', '\\', '\'', '"', 0},
                StringLiteralDecoder.decode("\"\\n\\t\\r\\b\\f\\\\\\'\\\"\\0\""));
    }

    @Test
    void usesMaximalNumericEscapeMatches() {
        assertDecodeError("\"\\018\"", StringLiteralDecoder.Reason.INVALID_NUMERIC_ESCAPE, 1);
        assertDecodeError("\"\\0xabcd\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
        assertDecodeError("\"\\0400\"", StringLiteralDecoder.Reason.NUMERIC_ESCAPE_OUT_OF_RANGE, 1);
    }

    @Test
    void rejectsMalformedEscapes() {
        assertDecodeError("\"\\0x\"", StringLiteralDecoder.Reason.INVALID_NUMERIC_ESCAPE, 1);
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
