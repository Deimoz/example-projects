package expression.expressions;

public class Const<E extends Number> implements CommonExpression<E> {
    private final E constValue;

    public Const(E constValue) {
        this.constValue = constValue;
    }

    @Override
    public E evaluate(E x, E y, E z) {
        return constValue;
    }

    @Override
    public String toString() {
        return String.valueOf(constValue);
    }
}
