package expression.parser;

import expression.expressions.CommonExpression;
import expression.exceptions.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser<E extends Number> {
    CommonExpression<E> parse(String expression) throws ParseExpressionException;
}