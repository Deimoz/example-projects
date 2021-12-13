import exceptions.LogicException;
import naturalOut.AxiomOutputs;
import naturalOut.BasicOutputs;
import parser.Parser;
import parser.expression.*;

import java.util.*;

public class MainTask {
    private final List<Expression> hyp;
    private final List<Expression> checked;
    private static List<Expression> axioms;

    public MainTask() {
        hyp = new ArrayList<>();
        checked = new ArrayList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        axioms = List.of(
                new Implication(a, new Implication(b, a)),
                new Implication(new Implication(a, b), new Implication(new Implication(a, new Implication(b, c)), new Implication(a, c))),
                new Implication(a, new Implication(b, new And(a, b))),
                new Implication(new And(a, b), a),
                new Implication(new And(a, b), b),
                new Implication(a, new Or(a, b)),
                new Implication(b, new Or(a, b)),
                new Implication(new Implication(a, c), new Implication(new Implication(b, c), new Implication(new Or(a, b), c))),
                new Implication(new Implication(a, b), new Implication(new Implication(a, new Not(b)), new Not(a))),
                new Implication(a, new Implication(new Not(a), b))
        );
    }

    private void treeDfs(Expression e, int i) {
        List<String> toWrite = new ArrayList<>();
        if (e.isMP) {
            for (int j = 0; j < e.getUsedSize(); j++) {
                treeDfs(e.getUsedById(j), i + 1);
            }
            toWrite.add(BasicOutputs.implicationOut(hyp, e.mp, e, i));
        } else if (e.isAxiom) {
            Map<String, Expression> vars = e.axiomsVars;
            switch (e.numOfAxiom) {
                case 0 -> {
                    toWrite = AxiomOutputs.axiom1(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 1 -> {
                    toWrite = AxiomOutputs.axiom2(hyp, vars.get("a"), vars.get("b"), vars.get("c"), i);
                }
                case 2 -> {
                    toWrite = AxiomOutputs.axiom3(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 3 -> {
                    toWrite = AxiomOutputs.axiom4(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 4 -> {
                    toWrite = AxiomOutputs.axiom5(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 5 -> {
                    toWrite = AxiomOutputs.axiom6(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 6 -> {
                    toWrite = AxiomOutputs.axiom7(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 7 -> {
                    toWrite = AxiomOutputs.axiom8(hyp, vars.get("a"), vars.get("b"), vars.get("c"), i);
                }
                case 8 -> {
                    toWrite = AxiomOutputs.axiom9(hyp, vars.get("a"), vars.get("b"), i);
                }
                case 9 -> {
                    toWrite = AxiomOutputs.axiom10(hyp, vars.get("a"), vars.get("b"), i);
                }
            }
        } else {
            toWrite.add(BasicOutputs.takeFromHypothesis(hyp, e, i));
        }
        for (String s : toWrite) {
            System.out.println(s);
        }
    }

    public void reconstruct(List<String> list) throws LogicException {
        int i = parseInput(list);
        treeDfs(checked.get(i), 0);
    }

    private int parseInput(List<String> list) throws LogicException {
        int indexOfProof = -1;
        String[] firstString = list.get(0).split("\\|-");
        String[] hyps = firstString[0].split(",");
        Parser parser = new Parser();
        Expression shouldBeProofed = parser.parse(firstString[1]);
        for (String s : hyps) {
            if (s.isEmpty()) {
                continue;
            }
            Expression expression = parser.parse(s);
            hyp.add(expression);
        }
        Expression last = null;
        boolean foundProof = false;

        for (int i = 1; i < list.size(); i++) {
            Expression res = parseLine(list.get(i));
            int num = i + 1;
            if (res == null) {
                throw new LogicException("Proof is incorrect at line " + num);
            }
            last = res;
            if (!foundProof && res.equals(shouldBeProofed)) {
                foundProof = true;
                indexOfProof = checked.size();
            }
            if (checked.contains(res)) {
                continue;
            }
            checked.add(res);
        }

        if (last != null && last.equals(shouldBeProofed)) {
            return indexOfProof;
        }
        throw new LogicException("The proof does not prove the required expression");
    }

    private Expression parseLine(String s) {
        Parser parser = new Parser();
        Expression expression = parser.parse(s);

        Expression res = isAxiom(expression);
        if (res != null) {
            return res;
        }
        res = isMP(expression);
        if (res != null) {
            return res;
        }
        res = isHyp(expression);
        return res;
    }

    private Expression isMP(Expression e) {
        List<Expression> available = new ArrayList<>();
        available.addAll(hyp);
        available.addAll(checked);

        for (Expression ex : available) {
            int index = available.indexOf(new Implication(ex, e));
            if (index == -1) {
                continue;
            }
            Expression mp = available.get(index);
            mp.setUsedIn(e);
            ex.setUsedIn(e);
            e.addUsed(mp);
            e.addUsed(ex);
            e.isMP = true;
            e.mp = ex;
            return e;
        }

        return null;
    }

    private Expression isHyp(Expression e) {
        if (hyp.contains(e)) {
            e.isHyp = true;
            return e;
        }
        return null;
    }

    private Expression isAxiom(Expression e) {
        for (int i = 0; i < axioms.size(); i++) {
            Map<String, Expression> result = compareToAxiom(e, axioms.get(i));
            if (result == null) {
                continue;
            }
            e.isAxiom = true;
            e.numOfAxiom = i;
            e.axiomsVars = result;
            return e;
        }
        return null;
    }

    private Map<String, Expression> compareToAxiom(Expression e, Expression axiom) {
        Map<String, Expression> vars = new HashMap<>();
        return compareToAxiom(e, axiom, vars);
    }

    private Map<String, Expression> compareToAxiom(Expression exprPart, Expression axiomPart, Map<String, Expression> vars) {
        if (axiomPart instanceof AbsBinExpression) {
            if (axiomPart.getClass() == exprPart.getClass()) {
                Map<String, Expression> result = compareToAxiom(((AbsBinExpression) exprPart).left, ((AbsBinExpression) axiomPart).left, vars);
                if (result == null) {
                    return null;
                }
                result = compareToAxiom(((AbsBinExpression) exprPart).right, ((AbsBinExpression) axiomPart).right, vars);
                return result;
            }
            return null;
        }
        if (axiomPart instanceof Not) {
            if (axiomPart.getClass() == exprPart.getClass()) {
                return compareToAxiom(((Not) exprPart).expression, ((Not) axiomPart).expression, vars);
            }
            return null;
        }
        if (axiomPart instanceof Variable) {
            String variable = ((Variable) axiomPart).name;
            if (vars.containsKey(variable)) {
                return vars.get(variable).equals(exprPart) ? vars : null;
            } else {
                vars.put(variable, exprPart);
            }
        }
        return vars;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> list = new ArrayList<>();
        String nextLine = scanner.nextLine();
        while (!nextLine.equals("")) {
            list.add(nextLine);
            nextLine = scanner.nextLine();
            if (!scanner.hasNextLine()) {
                if (!nextLine.equals("")) {
                    list.add(nextLine);
                }
                break;
            }
        }
        MainTask task = new MainTask();
        try {
            task.reconstruct(list);
        } catch (LogicException e) {
            System.out.println(e.getMessage());
        }
    }
}
