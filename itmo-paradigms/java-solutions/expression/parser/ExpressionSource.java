package expression.parser;

import expression.exceptions.ExpressionException;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    ExpressionException error(final String message);
    int posInSource();
    String parsedSourceData();
}
