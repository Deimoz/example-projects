package expression.expressions;

import expression.generic.NumberType;

public abstract class AbstractSingleExpression<E extends Number> implements CommonExpression<E> {
    private final CommonExpression<E> value;
    protected NumberType<E> opType;

    public AbstractSingleExpression(CommonExpression<E> value, NumberType<E> opType) {
        this.value = value;
        this.opType = opType;
    }

    protected abstract String getExpressionSymbol();

    protected abstract E countExpression(E x);

    @Override
    public E evaluate(E x, E y, E z) {
        return countExpression(value.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return getExpressionSymbol() + "(" + value.toString() + ")";
    }
}
