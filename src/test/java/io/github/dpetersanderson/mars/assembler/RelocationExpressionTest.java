package io.github.dpetersanderson.mars.assembler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.dpetersanderson.mars.ErrorList;
import io.github.dpetersanderson.mars.Globals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RelocationExpressionTest {
    @BeforeAll
    static void initializeMars() {
        Globals.initialize(false);
    }

    @Test
    void tokenizerKeepsRelocationExpressionsAsSingleTokens() {
        Tokenizer tokenizer = new Tokenizer();

        TokenList highTokens = tokenizer.tokenizeLine(1, "  lui $t0, %hi(pow2)");
        assertEquals(3, highTokens.size());
        assertToken(highTokens.get(2), TokenTypes.RELOCATION_HIGH, "%hi(pow2)", 12);

        TokenList lowTokens = tokenizer.tokenizeLine(2, "addiu $t0, $t0, %lo($L30-4)");
        assertEquals(4, lowTokens.size());
        assertToken(lowTokens.get(3), TokenTypes.RELOCATION_LOW, "%lo($L30-4)", 17);

        TokenList spacedTokens = tokenizer.tokenizeLine(3, "lui $t0, %hi(pow2 + 0x10)");
        assertEquals(3, spacedTokens.size());
        assertToken(spacedTokens.get(2), TokenTypes.RELOCATION_HIGH, "%hi(pow2 + 0x10)", 10);
        assertFalse(tokenizer.getErrors().errorsOccurred());
    }

    @Test
    void computesHighCarryLowSignExtensionAndWrappingAddends() throws Exception {
        assertEquals(0x1002, RelocationExpression.parse("%hi(pow2+4)").apply(0x10017ffc));
        assertEquals(-32768, RelocationExpression.parse("%lo(pow2 + 4)").apply(0x10017ffc));
        assertEquals(0x1001, RelocationExpression.parse("%hi(pow2)").apply(0x10010004));
        assertEquals(4, RelocationExpression.parse("%lo(pow2)").apply(0x10010004));
        assertEquals(-4, RelocationExpression.parse("%lo(pow2-4)").apply(0x10010000));
        assertEquals(0, RelocationExpression.parse("%lo(pow2+1)").apply(0xffffffff));
    }

    @Test
    void rejectsUnsupportedRelocationSyntaxWithLexicalLocations() {
        assertInvalidRelocation("lui $t0, %hi(symbol + value)", 10, "integer addend");
        assertInvalidRelocation("lui $t0, %hi(symbol", 10, "missing closing");
        assertInvalidRelocation("lui $t0, %hi(%lo(symbol))", 10, "nested expressions");
        assertInvalidRelocation("lui $t0, %hi(first-second)", 10, "integer addend");
        assertInvalidRelocation("lui $t0, %got(symbol)", 10, "unknown relocation operator");
    }

    private static void assertInvalidRelocation(String line, int column, String expectedMessage) {
        Tokenizer tokenizer = new Tokenizer();
        ErrorList errors = tokenizer.getErrors();

        tokenizer.tokenizeLine(7, line);

        assertTrue(errors.errorsOccurred());
        String report = errors.generateErrorReport();
        assertTrue(report.contains("line 7 column " + column), report);
        assertTrue(report.contains(expectedMessage), report);
    }

    private static void assertToken(Token token, TokenTypes type, String value, int column) {
        assertEquals(type, token.getType());
        assertEquals(value, token.getValue());
        assertEquals(column, token.getStartPos());
    }
}
