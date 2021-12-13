package expression.expressions;

import expression.exceptions.ArithmeticExpressionException;
import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalVariableException;
import expression.exceptions.OverflowException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface TripleExpression<E extends Number> extends ToMiniString {
    E evaluate(E x, E y, E z) throws ArithmeticExpressionException;
}