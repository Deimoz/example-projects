package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;

public class ShortType implements NumberType<Short> {

    @Override
    public Short add(Short x, Short y) {
        return (short) (x + y);
    }

    @Override
    public Short sub(Short x, Short y) {
        return (short) (x - y);
    }

    @Override
    public Short mul(Short x, Short y) {
        return (short) (x * y);
    }

    @Override
    public Short div(Short x, Short y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero: " + x + " / " + y);
        return (short) (x / y);
    }

    @Override
    public Short min(Short x, Short y) {
        if (x > y) {
            return y;
        };
        return x;
    }

    @Override
    public Short max(Short x, Short y) {
        if (x > y) {
            return x;
        };
        return y;
    }

    @Override
    public Short negate(Short x) {
        return (short) -x;
    }

    @Override
    public Short count(Short x) {
        // :NOTE: try to do it in alternative way
        return (short) Integer.bitCount(x & 0xFFFF);
    }

    @Override
    public Short parse(String number) throws IllegalNumberException {
        try {
            return (short) Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException("Invalid number: " + number);
        }
    }
}
