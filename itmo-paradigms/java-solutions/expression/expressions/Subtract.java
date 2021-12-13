package expression.expressions;

import expression.generic.NumberType;

public class Subtract<E extends Number> extends AbstractExpression<E> {
    public Subtract(CommonExpression<E> leftElement, CommonExpression<E> rightElement, NumberType<E> opType) {
        super(leftElement, rightElement, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return " - ";
    }

    @Override
    protected E countExpression(E x, E y) {
        return opType.sub(x, y);
    }
}
