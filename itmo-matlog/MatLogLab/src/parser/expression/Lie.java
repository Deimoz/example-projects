package parser.expression;

public class Lie extends Expression {
    @Override
    public String toString() {
        return "_|_";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lie;
    }
}
