package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;
import expression.exceptions.OverflowException;

public class IntegerType implements NumberType<Integer> {

    @Override
    public Integer add(Integer x, Integer y) throws OverflowException {
        if ((x > 0 && y > 0 && Integer.MAX_VALUE - x < y) ||
            (x < 0 && y < 0 && Integer.MIN_VALUE - x > y)) {
            throw new OverflowException("Add overflow: " + x + " + " + y);
        }
        return x + y;
    }

    @Override
    public Integer sub(Integer x, Integer y) {
        if ((x - y > x && y > 0) || (x - y < x && y < 0)) {
            throw new OverflowException("Subtract overflow: " + x + " - " + y);
        }
        return x - y;
    }

    @Override
    public Integer mul(Integer x, Integer y) {
        if (x > 0) {
            if ((y < 0 && Integer.MIN_VALUE / x > y) ||
                    (y > 0 && Integer.MAX_VALUE / x < y)) {
                throw new OverflowException("Multiply overflow: " + x + " * " + y);
            }
        } else if (x < 0) {
            if ((y < 0 && Integer.MAX_VALUE / x > y) ||
                    (y > 0 && Integer.MIN_VALUE / y > x)) {
                throw new OverflowException("Multiply overflow:" + x + " * " + y);
            }
        }
        return x * y;
    }

    @Override
    public Integer div(Integer x, Integer y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero: " + x + " / " + y);
        if (x == Integer.MIN_VALUE && y == -1) throw new OverflowException("Divide overflow: " + x + " / " + y );
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
        if (x == Integer.MIN_VALUE) throw new OverflowException("Negate overflow");
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
