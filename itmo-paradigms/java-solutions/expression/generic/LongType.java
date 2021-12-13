package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;

public class LongType implements NumberType<Long> {
    @Override
    public Long add(Long x, Long y) {
        return x + y;
    }

    @Override
    public Long sub(Long x, Long y) {
        return x - y;
    }

    @Override
    public Long mul(Long x, Long y) {
        return x * y;
    }

    @Override
    public Long div(Long x, Long y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero: " + x + " / " + y);
        return x / y;
    }

    @Override
    public Long min(Long x, Long y) {
        if (x > y) {
            return y;
        };
        return x;
    }

    @Override
    public Long max(Long x, Long y) {
        if (x > y) {
            return x;
        };
        return y;
    }

    @Override
    public Long negate(Long x) {
        return -x;
    }

    @Override
    public Long count(Long x) {
        return (long) Long.bitCount(x);
    }

    @Override
    public Long parse(String number) throws IllegalNumberException {
        try {
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException("Invalid number: " + number);
        }
    }
}
