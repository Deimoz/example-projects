package parser.expression;

public abstract class AbsBinExpression extends Expression {
    public Expression left;
    public Expression right;

    public AbsBinExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    abstract String getSymbol();

    @Override
    public String toString() {
        return "(" + left.toString() + getSymbol() + right.toString() + ")";
    }

    @Override
    public int hashCode() {
        return left.hashCode() * 13 + getSymbol().hashCode() * 17 + right.hashCode() * 23;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbsBinExpression) {
            AbsBinExpression other = (AbsBinExpression) obj;
            if (other.getSymbol().equals(getSymbol())) {
                return (left.equals(other.left) && right.equals(other.right));
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
