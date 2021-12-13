package expression.parser;

import expression.exceptions.*;
import expression.expressions.Const;
import expression.generic.NumberType;

public abstract class BaseParser<E extends Number> {
    private final ExpressionSource source;
    protected char ch;
    protected NumberType<E> opType;

    public BaseParser(final ExpressionSource source, NumberType<E> opType) {
        this.source = source;
        ch = source.next();
        this.opType = opType;
    }

    protected void nextChar() {
        ch = (source.hasNext()) ? source.next() : '\0';
    }

    protected void skipWhitespace() {
        while(Character.isWhitespace(ch)) {
            test(ch);
        }
    }

    protected boolean test(char testValue) {
        if (ch == testValue) {
            nextChar();
            return true;
        }
        return false;
    }

    protected boolean test(final String testValue) {
        boolean passed = true;
        for (char c : testValue.toCharArray()) {
            passed = passed && test(c);
        }
        return passed;
    }

    protected void expect(final char c) throws ParseExpressionException {
        if (ch != c) {
            if (ch == ')') throw new MissingBracketException("No open bracket on position" + positionInfo());
            throw new IllegalSymbolException("Expected '" + c + "', found '" + ch + "' on position " + positionInfo());
        }
        nextChar();
    }

    protected void expect(final String value) throws ParseExpressionException {
        for (char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected void expectEnd() throws ParseExpressionException {
        if (ch != '\0') {
            throw new IllegalSymbolException("End of expression expected, found '" + ch + "' on position " + positionInfo());
        }
    }

    // :NOTE: no parameterization
    protected Const parseNumber(boolean withUnaryMinus) throws ParseExpressionException {
        final StringBuilder sb = new StringBuilder();
        if (withUnaryMinus) {
            sb.append('-');
        }
        if (test('0')) {
            sb.append('0');
        } else if (between('1', '9')) {
            while (between('0', '9')) {
                sb.append(ch);
                nextChar();
            }
        } else {
            throw new IllegalSymbolException("Not a number on position " + positionInfo());
        }

        return new Const<>(opType.parse(sb.toString()));
    }

    protected boolean between(char from, char to) {
        return from <= ch && ch <= to;
    }

    protected ExpressionException error(final String message) {
        return source.error(message);
    }

    protected String positionInfo() {
        return source.posInSource() + " in expression: '" + source.parsedSourceData() + "'";
    }
}