package expression.exceptions;

import expression.exceptions.ExpressionException;

public class DivisionByZeroException extends ArithmeticExpressionException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
