package parser.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Expression {
    public abstract String toString();

    private Expression usedIn;
    private final List<Expression> used = new ArrayList<>();
    public int numOfAxiom = -1;
    public Map<String, Expression> axiomsVars;
    public boolean isMP = false;
    public Expression mp = null;
    public boolean isHyp = false;
    public boolean isAxiom = false;

    public void addUsed(Expression u) {
        used.add(u);
    }

    public Expression getUsedById(int i) {
        return used.get(i);
    }

    public int getUsedSize() {
        return used.size();
    }

    public Expression getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(Expression u) {
        usedIn = u;
    }
}
