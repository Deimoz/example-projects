package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IllegalNumberException;

import java.math.BigInteger;

public class BigIntegerType implements NumberType<BigInteger> {

    @Override
    public BigInteger add(BigInteger x, BigInteger y) {
        return x.add(y);
    }

    @Override
    public BigInteger sub(BigInteger x, BigInteger y) {
        return x.add(y.negate());
    }

    @Override
    public BigInteger mul(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }

    @Override
    public BigInteger div(BigInteger x, BigInteger y) {
        if (y.equals(BigInteger.ZERO)) throw new DivisionByZeroException("Division by zero: " + x + " / " + y);
        return x.divide(y);
    }

    @Override
    public BigInteger min(BigInteger x, BigInteger y) {
        return x.min(y);
    }

    @Override
    public BigInteger max(BigInteger x, BigInteger y) {
        return x.max(y);
    }

    @Override
    public BigInteger negate(BigInteger x) {
        return x.negate();
    }

    @Override
    public BigInteger count(BigInteger x) {
        return BigInteger.valueOf(x.bitCount());
    }

    @Override
    public BigInteger parse(String number) throws IllegalNumberException {
        try {
            return new BigInteger(number);
        } catch (NumberFormatException e) {
            throw new IllegalNumberException("Invalid number: " + number);
        }
    }
}
