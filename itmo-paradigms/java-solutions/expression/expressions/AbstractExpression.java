package expression.expressions;

import expression.generic.NumberType;

public abstract class AbstractExpression<E extends Number> implements CommonExpression<E> {
    private CommonExpression<E> leftElement, rightElement;
    protected NumberType<E> opType;

    protected AbstractExpression(CommonExpression<E> leftElement, CommonExpression<E> rightElement, NumberType<E> opType) {
        this.leftElement = leftElement;
        this.rightElement = rightElement;
        this.opType = opType;
    }

    protected abstract String getExpressionSymbol();

    @Override
    public String toString() {
        return "(" + leftElement + getExpressionSymbol() + rightElement + ")";
    }


    protected abstract E countExpression(E x, E y);

    @Override
    public E evaluate(E x, E y, E z) {
        return countExpression(leftElement.evaluate(x ,y, z), rightElement.evaluate(x, y, z));
    }
}
