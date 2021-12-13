package expression.generic;

import expression.expressions.CommonExpression;
import expression.exceptions.*;
import expression.parser.ExpressionParser;
import expression.parser.Parser;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return createTable(takeOp(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <E extends Number> Object[][][] createTable(NumberType<E> opType, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParseExpressionException {
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        Parser<E> parser = new ExpressionParser<>(opType);
        CommonExpression<E> parsedExpression = parser.parse(expression);
        for (int i = 0; i <= x2 - x1; i++) {
            for (int j = 0; j <= y2 - y1; j++) {
                for (int k = 0; k <= z2 - z1; k++) {
                    try {
                        table[i][j][k] = parsedExpression.evaluate(
                                opType.parse(String.valueOf(i + x1)),
                                opType.parse(String.valueOf(j + y1)),
                                opType.parse(String.valueOf(k + z1))
                        );
                    } catch (ArithmeticExpressionException | ParseExpressionException e) {
                        table[i][j][k] = null;
                    }
                }
            }
        }
        return table;
    }

    // :NOTE: where is a bounds of wildcard (?) type? I can divide String by String?
    private NumberType<? extends Number> takeOp(String mode) throws IllegalSymbolException {
        switch (mode) {
            case "i":
                return new IntegerType();
            case "d":
                return new DoubleType();
            case "bi":
                return new BigIntegerType();
            case "u":
                return new UIntType();
            case "l":
                return new LongType();
            case "s":
                return new ShortType();
            default:
                throw new IllegalSymbolException("Wrong type of expression: " + mode);
        }
    }
}
