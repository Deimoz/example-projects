package expression.expressions;

import expression.generic.NumberType;

public class Count<E extends Number> extends AbstractSingleExpression<E> {
    public Count(CommonExpression<E> value, NumberType<E> opType) {
        super(value, opType);
    }

    @Override
    protected String getExpressionSymbol() {
        return "count ";
    }

    @Override
    protected E countExpression(E x) {
        return opType.count(x);
    }
}
