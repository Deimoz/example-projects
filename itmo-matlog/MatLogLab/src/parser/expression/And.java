package parser.expression;

public class And extends AbsBinExpression {
    public And(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    String getSymbol() {
        return "&";
    }
}
