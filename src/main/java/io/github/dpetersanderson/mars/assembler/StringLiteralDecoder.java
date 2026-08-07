package io.github.dpetersanderson.mars.assembler;

import java.io.ByteArrayOutputStream;

/** Decodes the contents of an ASCII or ASCIIZ string literal. */
final class StringLiteralDecoder {
    private StringLiteralDecoder() {}

    static byte[] decode(String literal) throws DecodeException {
        if (literal.length() < 2 || literal.charAt(0) != '"' || literal.charAt(literal.length() - 1) != '"') {
            throw new DecodeException(Reason.INVALID_LITERAL, 0, "string literal is missing a closing quote");
        }

        ByteArrayOutputStream decoded = new ByteArrayOutputStream(literal.length() - 2);
        for (int index = 1; index < literal.length() - 1; index++) {
            char character = literal.charAt(index);
            if (character != '\\') {
                decoded.write(character);
                continue;
            }

            DecodedEscape escape = decodeEscape(literal, index, literal.length() - 1);
            decoded.write(escape.value());
            index = escape.endIndex() - 1;
        }
        return decoded.toByteArray();
    }

    /**
     * Decodes the escape sequence that begins at the given backslash and returns its byte value
     * together with the index of the first character that follows the escape.
     *
     * <p>Recognized forms are the simple escapes {@code \n}, {@code \t}, {@code \r}, {@code \b},
     * {@code \f}, {@code \\}, {@code \'}, {@code \"}, the octal escape {@code \ooo} (one to three
     * octal digits, truncated at the first non-octal digit) and the hexadecimal escape {@code \xhh}
     * (one or more consecutive hexadecimal digits).
     *
     * @param literal source text containing the escape
     * @param escapeIndex position of the backslash within {@code literal}
     * @param contentEnd exclusive end of the content region within {@code literal}
     * @return the decoded byte value and the index just past the escape
     * @throws DecodeException if the escape is incomplete, unknown or its value exceeds the byte range
     */
    static DecodedEscape decodeEscape(String literal, int escapeIndex, int contentEnd) throws DecodeException {
        int index = escapeIndex + 1;
        if (index >= contentEnd) {
            throw new DecodeException(
                    Reason.INCOMPLETE_ESCAPE, escapeIndex, "incomplete escape sequence in string literal");
        }

        char character = literal.charAt(index);
        return switch (character) {
            case 'n' -> new DecodedEscape('\n', index + 1);
            case 't' -> new DecodedEscape('\t', index + 1);
            case 'r' -> new DecodedEscape('\r', index + 1);
            case 'b' -> new DecodedEscape('\b', index + 1);
            case 'f' -> new DecodedEscape('\f', index + 1);
            case '\\', '\'', '"' -> new DecodedEscape(character, index + 1);
            case 'x', 'X' -> decodeHexEscape(literal, escapeIndex, index, contentEnd);
            default -> {
                if (character >= '0' && character <= '7') {
                    yield decodeOctalEscape(literal, escapeIndex, index, contentEnd);
                }
                throw new DecodeException(
                        Reason.UNKNOWN_ESCAPE, escapeIndex, "unknown escape sequence \"\\" + character + "\"");
            }
        };
    }

    private static DecodedEscape decodeOctalEscape(String literal, int escapeIndex, int index, int contentEnd)
            throws DecodeException {
        int value = 0;
        int digits = 0;
        while (index < contentEnd && digits < 3) {
            char digit = literal.charAt(index);
            if (digit < '0' || digit > '7') {
                break;
            }
            value = value * 8 + (digit - '0');
            index++;
            digits++;
        }
        if (value > 0xff) {
            throw new DecodeException(
                    Reason.NUMERIC_ESCAPE_OUT_OF_RANGE,
                    escapeIndex,
                    "octal escape \"\\" + literal.substring(escapeIndex + 1, index)
                            + "\" is outside the byte range 0..255");
        }
        return new DecodedEscape(value, index);
    }

    private static DecodedEscape decodeHexEscape(String literal, int escapeIndex, int index, int contentEnd)
            throws DecodeException {
        int digitStart = index + 1;
        index = digitStart;
        int value = 0;
        while (index < contentEnd && isHexDigit(literal.charAt(index))) {
            char digit = literal.charAt(index);
            if (value <= 0xff) {
                value = value * 16 + Character.digit(digit, 16);
            }
            index++;
        }
        if (index == digitStart) {
            throw new DecodeException(
                    Reason.INVALID_NUMERIC_ESCAPE, escapeIndex, "hexadecimal escape requires at least one digit");
        }
        if (value > 0xff) {
            throw new DecodeException(
                    Reason.NUMERIC_ESCAPE_OUT_OF_RANGE,
                    escapeIndex,
                    "hexadecimal escape \"\\x" + literal.substring(digitStart, index)
                            + "\" is outside the byte range 0..255");
        }
        return new DecodedEscape(value, index);
    }

    private static boolean isHexDigit(char character) {
        return character >= '0' && character <= '9'
                || character >= 'a' && character <= 'f'
                || character >= 'A' && character <= 'F';
    }

    /** The result of decoding one escape sequence. */
    static final class DecodedEscape {
        private final int value;
        private final int endIndex;

        private DecodedEscape(int value, int endIndex) {
            this.value = value;
            this.endIndex = endIndex;
        }

        int value() {
            return value;
        }

        int endIndex() {
            return endIndex;
        }
    }

    enum Reason {
        INVALID_LITERAL,
        INCOMPLETE_ESCAPE,
        UNKNOWN_ESCAPE,
        INVALID_NUMERIC_ESCAPE,
        NUMERIC_ESCAPE_OUT_OF_RANGE
    }

    /** Indicates an invalid string literal and records its offset within the source token. */
    static final class DecodeException extends Exception {
        private final Reason reason;
        private final int offset;

        private DecodeException(Reason reason, int offset, String message) {
            super(message);
            this.reason = reason;
            this.offset = offset;
        }

        Reason getReason() {
            return reason;
        }

        int getOffset() {
            return offset;
        }
    }
}
