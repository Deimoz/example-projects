package expression.expressions;

import expression.generic.NumberType;

public class Max<E extends Number> extends AbstractExpression<E> {
    public Max(CommonExpression<E> leftElement, CommonExpression<E> rightElement, NumberType<E> opType) {
        super(leftElement, rightElement, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return " max ";
    }

    @Override
    protected E countExpression(E x, E y) {
        return opType.max(x, y);
    }
}
