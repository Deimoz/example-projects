package parser.expression;

public class Or extends AbsBinExpression {
    public Or(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    String getSymbol() {
        return "|";
    }
}
