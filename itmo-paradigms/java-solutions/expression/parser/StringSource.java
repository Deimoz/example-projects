package expression.parser;

import expression.exceptions.ExpressionException;

public class StringSource implements ExpressionSource {
    private final String data;
    private int pos;

    public StringSource(final String data) {
        this.data = data;
        pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public ExpressionException error(final String message) {
        return new ExpressionException(pos + ": " + message);
    }

    @Override
    public int posInSource() {
        return pos;
    }

    @Override
    public String parsedSourceData() {
        return data.substring(0, pos);
    }
}
