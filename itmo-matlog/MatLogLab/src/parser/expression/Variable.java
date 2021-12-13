package parser.expression;

public class Variable extends Expression {
    public String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            return name.equals(obj.toString());
        } else {
            return false;
        }
    }
}
