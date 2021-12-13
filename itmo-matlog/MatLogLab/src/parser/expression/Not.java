package parser.expression;

public class Not extends Expression {
    public Expression expression;

    public Not(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "(" + expression.toString() + "->_|_)";
    }

    @Override
    public int hashCode() {
        return expression.hashCode() * 7 + 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Not) {
            return expression.equals(((Not) obj).expression);
        } else {
            return false;
        }
    }
}
