package expression.expressions;

import expression.generic.NumberType;

public class Min<E extends Number> extends AbstractExpression<E> {
    public Min(CommonExpression<E> leftElement, CommonExpression<E> rightElement, NumberType<E> opType) {
        super(leftElement, rightElement, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return " min ";
    }

    @Override
    protected E countExpression(E x, E y) {
        return opType.min(x, y);
    }
}
