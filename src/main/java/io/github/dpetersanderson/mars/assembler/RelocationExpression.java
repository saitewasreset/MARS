package io.github.dpetersanderson.mars.assembler;

import io.github.dpetersanderson.mars.util.Binary;

/** Parsed MIPS absolute-symbol relocation expression. */
final class RelocationExpression {
    private static final String HIGH_OPERATOR = "%hi";
    private static final String LOW_OPERATOR = "%lo";

    private final TokenTypes type;
    private final String symbol;
    private final int addend;

    private RelocationExpression(TokenTypes type, String symbol, int addend) {
        this.type = type;
        this.symbol = symbol;
        this.addend = addend;
    }

    static RelocationExpression parse(String value) throws ParseException {
        TokenTypes type;
        int operatorLength;
        if (value.startsWith(HIGH_OPERATOR)) {
            type = TokenTypes.RELOCATION_HIGH;
            operatorLength = HIGH_OPERATOR.length();
        } else if (value.startsWith(LOW_OPERATOR)) {
            type = TokenTypes.RELOCATION_LOW;
            operatorLength = LOW_OPERATOR.length();
        } else {
            throw new ParseException("unknown relocation operator");
        }

        if (value.length() <= operatorLength || value.charAt(operatorLength) != '(') {
            throw new ParseException("expected '(' after relocation operator");
        }
        if (value.charAt(value.length() - 1) != ')') {
            throw new ParseException("missing closing ')'");
        }

        String expression =
                value.substring(operatorLength + 1, value.length() - 1).trim();
        if (expression.indexOf('(') >= 0 || expression.indexOf(')') >= 0) {
            throw new ParseException("nested expressions are not supported");
        }

        int operatorIndex = findAddendOperator(expression);
        String symbol = (operatorIndex < 0 ? expression : expression.substring(0, operatorIndex)).trim();
        if (symbol.isEmpty() || !TokenTypes.isValidIdentifier(symbol)) {
            throw new ParseException("expected one symbol");
        }

        int addend = 0;
        if (operatorIndex >= 0) {
            char operator = expression.charAt(operatorIndex);
            String addendText = expression.substring(operatorIndex + 1).trim();
            if (addendText.isEmpty() || containsWhitespace(addendText)) {
                throw new ParseException("expected an integer addend");
            }
            try {
                int magnitude = Binary.stringToInt(addendText);
                addend = operator == '+' ? magnitude : -magnitude;
            } catch (NumberFormatException exception) {
                throw new ParseException("expected an integer addend", exception);
            }
        }
        return new RelocationExpression(type, symbol, addend);
    }

    private static int findAddendOperator(String expression) throws ParseException {
        int operatorIndex = -1;
        for (int index = 1; index < expression.length(); index++) {
            char character = expression.charAt(index);
            if (character == '+' || character == '-') {
                if (operatorIndex >= 0) {
                    throw new ParseException("only one integer addend is supported");
                }
                operatorIndex = index;
            }
        }
        return operatorIndex;
    }

    private static boolean containsWhitespace(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (Character.isWhitespace(value.charAt(index))) {
                return true;
            }
        }
        return false;
    }

    TokenTypes getType() {
        return type;
    }

    String getSymbol() {
        return symbol;
    }

    int apply(int symbolAddress) {
        int value = symbolAddress + addend;
        if (type == TokenTypes.RELOCATION_HIGH) {
            return ((value >> 16) + ((value >>> 15) & 1)) & 0xffff;
        }
        return (short) value;
    }

    /** Indicates a syntactically invalid relocation expression. */
    static final class ParseException extends Exception {
        private ParseException(String message) {
            super(message);
        }

        private ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
