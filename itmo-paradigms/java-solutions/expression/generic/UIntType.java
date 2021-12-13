package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;

public class UIntType implements NumberType<Integer>{
    @Override
    public Integer add(Integer x, Integer y) {
        return x + y;
    }

    @Override
    public Integer sub(Integer x, Integer y) {
        return x - y;
    }

    @Override
    public Integer mul(Integer x, Integer y) {
        return x * y;
    }

    @Override
    public Integer div(Integer x, Integer y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero: " + x + " / " + y);
        return x / y;
    }

    @Override
    public Integer min(Integer x, Integer y) {
        if (x > y) {
            return y;
        };
        return x;
    }

    @Override
    public Integer max(Integer x, Integer y) {
        if (x > y) {
            return x;
        };
        return y;
    }

    @Override
    public Integer negate(Integer x) {
        return -x;
    }

    @Override
    public Integer count(Integer x) {
        return Integer.bitCount(x);
    }

    @Override
    public Integer parse(String number) throws IllegalNumberException {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException("Invalid number: " + number);
        }
    }
}
