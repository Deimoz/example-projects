package parser.expression;

public class Implication extends AbsBinExpression {
    public Implication(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    String getSymbol() {
        return "->";
    }
}
