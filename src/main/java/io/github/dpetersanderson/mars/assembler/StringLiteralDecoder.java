package io.github.dpetersanderson.mars.assembler;

import io.github.dpetersanderson.mars.util.Binary;
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

            int escapeOffset = index;
            index++;
            if (index >= literal.length() - 1) {
                throw new DecodeException(
                        Reason.INCOMPLETE_ESCAPE, escapeOffset, "incomplete escape sequence in string literal");
            }

            character = literal.charAt(index);
            switch (character) {
                case 'n':
                    decoded.write('\n');
                    break;
                case 't':
                    decoded.write('\t');
                    break;
                case 'r':
                    decoded.write('\r');
                    break;
                case '\\', '\'', '"':
                    decoded.write(character);
                    break;
                case 'b':
                    decoded.write('\b');
                    break;
                case 'f':
                    decoded.write('\f');
                    break;
                case '0':
                    int numericEnd = scanNumericEscapeEnd(literal, index);
                    String numericLiteral = literal.substring(index, numericEnd);
                    int value;
                    try {
                        value = Binary.stringToInt(numericLiteral);
                    } catch (NumberFormatException exception) {
                        throw new DecodeException(
                                Reason.INVALID_NUMERIC_ESCAPE,
                                escapeOffset,
                                "invalid numeric escape \"\\" + numericLiteral + "\"",
                                exception);
                    }
                    if (value < 0 || value > 0xff) {
                        throw new DecodeException(
                                Reason.NUMERIC_ESCAPE_OUT_OF_RANGE,
                                escapeOffset,
                                "numeric escape \"\\" + numericLiteral + "\" is outside the byte range 0..255");
                    }
                    decoded.write(value);
                    index = numericEnd - 1;
                    break;
                default:
                    throw new DecodeException(
                            Reason.UNKNOWN_ESCAPE, escapeOffset, "unknown escape sequence \"\\" + character + "\"");
            }
        }
        return decoded.toByteArray();
    }

    private static int scanNumericEscapeEnd(String literal, int zeroIndex) throws DecodeException {
        int contentEnd = literal.length() - 1;
        int index = zeroIndex + 1;
        if (index < contentEnd && (literal.charAt(index) == 'x' || literal.charAt(index) == 'X')) {
            int digitStart = ++index;
            while (index < contentEnd && isHexDigit(literal.charAt(index))) {
                index++;
            }
            if (index == digitStart) {
                throw new DecodeException(
                        Reason.INVALID_NUMERIC_ESCAPE, zeroIndex - 1, "hexadecimal escape requires at least one digit");
            }
            return index;
        }
        while (index < contentEnd && Character.isDigit(literal.charAt(index))) {
            index++;
        }
        return index;
    }

    private static boolean isHexDigit(char character) {
        return character >= '0' && character <= '9'
                || character >= 'a' && character <= 'f'
                || character >= 'A' && character <= 'F';
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

        private DecodeException(Reason reason, int offset, String message, Throwable cause) {
            super(message, cause);
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
