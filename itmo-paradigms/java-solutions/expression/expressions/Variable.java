package expression.expressions;

import expression.exceptions.IllegalVariableException;

public class Variable<E extends Number> implements CommonExpression<E> {
    private final String variableName;

    public Variable(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public E evaluate(E x, E y, E z) throws IllegalVariableException {
        switch (variableName) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new IllegalVariableException("Wrong variable name: " + variableName);
    }

    @Override
    public String toString() {
        return variableName;
    }
}
