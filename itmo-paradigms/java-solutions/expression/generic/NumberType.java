package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;
import expression.exceptions.OverflowException;

public interface NumberType<E extends Number> {
    E add(E x, E y);
    E sub(E x, E y);
    E mul(E x, E y);
    E div(E x, E y);
    E min(E x, E y);
    E max(E x, E y);
    E negate(E x);
    E count(E x);
    E parse(String number) throws IllegalNumberException;
}
