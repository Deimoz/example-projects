package expression.expressions;

import expression.generic.NumberType;

public class Multiply<E extends Number> extends AbstractExpression<E> {
    public Multiply(CommonExpression<E> leftElement, CommonExpression<E> rightElement, NumberType<E> opType) {
        super(leftElement, rightElement, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return " * ";
    }

    @Override
    protected E countExpression(E x, E y) {
        return opType.mul(x, y);
    }
}
