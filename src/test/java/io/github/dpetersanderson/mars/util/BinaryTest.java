package io.github.dpetersanderson.mars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BinaryTest {
    @Test
    void parsesDecimalOctalAndHexadecimalIntegers() {
        assertEquals(12, Binary.stringToInt("12"));
        assertEquals(10, Binary.stringToInt("012"));
        assertEquals(-10, Binary.stringToInt("-012"));
        assertEquals(0xabcd, Binary.stringToInt("0xabcd"));
        assertEquals(0xff, Binary.stringToInt("+0XFF"));
    }

    @Test
    void parsesFullUnsignedBitPatterns() {
        assertEquals(-1, Binary.stringToInt("4294967295"));
        assertEquals(-1, Binary.stringToInt("037777777777"));
        assertEquals(-1, Binary.stringToInt("0xffffffff"));
        assertEquals(Integer.MIN_VALUE, Binary.stringToInt("-020000000000"));
    }

    @Test
    void rejectsInvalidOrOutOfRangeIntegers() {
        assertInvalid("");
        assertInvalid("+");
        assertInvalid("-");
        assertInvalid("018");
        assertInvalid("0x");
        assertInvalid("0xgg");
        assertInvalid("4294967296");
        assertInvalid("040000000000");
        assertInvalid("-2147483649");
    }

    private static void assertInvalid(String value) {
        assertThrows(NumberFormatException.class, () -> Binary.stringToInt(value));
    }
}
