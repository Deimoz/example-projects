package expression.expressions;

import expression.generic.NumberType;

public class Negate<E extends Number> extends AbstractSingleExpression<E> {

    public Negate(CommonExpression<E> value, NumberType<E> opType) {
        super(value, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return "-";
    }

    @Override
    protected E countExpression(E x) {
        return opType.negate(x);
    }
}
