package naturalOut;

import parser.expression.*;

import java.util.ArrayList;
import java.util.List;

public class AxiomOutputs {
    public static List<Integer> stepsInTree = List.of(2, 6, 3, 2, 2, 2, 2 ,5, 6, 4);

    /**
     * [i + 2] Г, a, b |- a [Ax]
     * [i + 1] Г, a |- b -> a [I->]
     * [i] Г |- a -> b -> a [I->]
     *
     * a -> b -> a
     */
    public static List<String> axiom1(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        hypothesises.add(a); hypothesises.add(b);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, b, a, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, a, new Implication(b, a), i));
        return result;
    }

    /**
     * [i + 5] Г, a, a->b, a->b->c |- a->b->c [Ax]
     * [i + 5] Г, a, a->b, a->b->c |- a [Ax]
     * [i + 4] Г, a, a->b, a->b->c |- b->c [E->]
     * [i + 5] Г, a, a->b, a->b->c |- a->b [Ax]
     * [i + 5] Г, a, a->b, a->b->c |- a [Ax]
     * [i + 4] Г, a, a->b, a->b->c |- b [E->]
     * [i + 3] Г, a, a->b, a->b->c |- c [E->]
     * [i + 2] Г, a->b, a->b->c |- a->c [I->]
     * [i + 1] Г, a->b |-  (a->b->c)->(a->c) [I->]
     * [i] Г |- (a->b)-> (a->b->c)->(a->c) [I->]
     *
     * (a->b)-> (a->b->c)->(a->c)
     */
    public static List<String> axiom2(List<Expression> hypothesises, Expression a, Expression b, Expression c, int i) {
        List<String> result = new ArrayList<>();
        Expression ab = new Implication(a, b);
        Expression ac = new Implication(a, c);
        Expression bc = new Implication(b, c);
        Expression abc = new Implication(a, bc);
        hypothesises.add(a); hypothesises.add(ab); hypothesises.add(abc);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, abc, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, a, bc, i + 4));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, ab, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, a, b, i + 4));
        result.add(BasicOutputs.implicationOut(hypothesises, b, c, i + 3));
        result.add(BasicOutputs.implicationIn(hypothesises, a, c, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, abc, ac, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, ab, new Implication(abc, ac), i));
        return result;
    }

    /**
     * [i + 3] Г, a, b |- a [Ax]
     * [i + 3] Г, a, b |- b [Ax]
     * [i + 2] Г, a, b |- a & b [I&]
     * [i + 1] Г, a |- b -> a & b [I->]
     * [i] Г |- a -> b -> a & b [I->]
     *
     * a -> b -> a & b
     */
    public static List<String> axiom3(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        hypothesises.add(a); hypothesises.add(b);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 3));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, b, i + 3));
        result.add(BasicOutputs.andIn(hypothesises, a, b, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, b, new And(a, b), i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, a, new Implication(b, new And(a, b)), i));
        return result;
    }

    /**
     * [i + 2] Г, a & b |- a & b [Ax]
     * [i + 1] Г, a & b |- a [El&]
     * [i] Г |- a & b -> a [I->]
     *
     * a & b -> a
     */
    public static List<String> axiom4(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        Expression aAndB = new And(a, b);
        hypothesises.add(aAndB);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, aAndB, i + 2));
        result.add(BasicOutputs.andOutLeft(hypothesises, a, b, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, new And(a, b), a, i));
        return result;
    }

    /**
     * [i + 2] Г, a & b |- a & b [Ax]
     * [i + 1] Г, a & b |- b [Er&]
     * [i] Г |- a & b -> b [I->]
     *
     * a & b -> b
     */
    public static List<String> axiom5(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        Expression aAndB = new And(a, b);
        hypothesises.add(aAndB);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, aAndB, i + 2));
        result.add(BasicOutputs.andOutRight(hypothesises, a, b, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, new And(a, b), b, i));
        return result;
    }

    /**
     * [i + 2] Г, a |- a [Ax]
     * [i + 1] Г, a |- a | b [Il|]
     * [i] Г |- a -> a | b [I->]
     *
     * a -> a | b
     */
    public static List<String> axiom6(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        hypothesises.add(a);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 2));
        result.add(BasicOutputs.orInLeft(hypothesises, a, b, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, a, new Or(a, b), i));
        return result;
    }

    /**
     * [i + 2] Г, b |- b [Ax]
     * [i + 1] Г, b |- a | b [Ir|]
     * [i] Г |- b -> a | b [I->]
     *
     * b -> a | b
     */
    public static List<String> axiom7(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        hypothesises.add(b);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, b, i + 2));
        result.add(BasicOutputs.orInRight(hypothesises, a, b, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, b, new Or(a, b), i));
        return result;
    }

    /**
     * [i + 5] Г, a -> c, b -> c, a | b, a |- a -> c [Ax]
     * [i + 5] Г, a -> c, b -> c, a | b, a |- a [Ax]
     * [i + 4] Г, a -> c, b -> c, a | b, a |- c [E->]
     * [i + 5] Г, a -> c, b -> c, a | b, b |- b -> c [Ax]
     * [i + 5] Г, a -> c, b -> c, a | b, b |- b [Ax]
     * [i + 4] Г, a -> c, b -> c, a | b, b |- c [E->]
     * [i + 4] Г, a -> c, b -> c, a | b |- a | b [Ax]
     * [i + 3] Г, a -> c, b -> c, a | b |- c [E|]
     * [i + 2] Г, a -> c, b -> c |- (a | b -> c) [I->]
     * [i + 1] Г, a -> c |- (b -> c) -> (a | b -> c) [I->]
     * [i] Г |- (a -> c) -> (b -> c) -> (a | b -> c) [I->]
     *
     * (a -> c) -> (b -> c) -> (a | b -> c)
     */
    public static List<String> axiom8(List<Expression> hypothesises, Expression a, Expression b, Expression c, int i) {
        List<String> result = new ArrayList<>();
        Expression ac = new Implication(a, c);
        Expression bc = new Implication(b, c);
        Expression aOrB = new Or(a, b);
        hypothesises.add(ac); hypothesises.add(bc); hypothesises.add(aOrB); hypothesises.add(a);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, ac, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, a, c, i + 4));
        hypothesises.remove(a);
        hypothesises.add(b);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, bc, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, b, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, b, c, i + 4));
        hypothesises.remove(b);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, aOrB, i + 4));
        result.add(BasicOutputs.orOut(hypothesises, a, b, c, i + 3));
        result.add(BasicOutputs.implicationIn(hypothesises, aOrB, c, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, bc, new Implication(aOrB, c), i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, ac, new Implication(bc, new Implication(aOrB, c)), i));
        return result;
    }

    /**
     * [i + 5] Г, a, a -> b, a -> (b -> _|_) |- a -> (b -> _|_) [Ax]
     * [i + 5] Г, a, a -> b, a -> (b -> _|_) |- a [Ax]
     * [i + 4] Г, a, a -> b, a -> (b -> _|_) |- b -> _|_ [E->]
     * [i + 5] Г, a, a -> b, a -> (b -> _|_) |- a -> b [Ax]
     * [i + 5] Г, a, a -> b, a -> (b -> _|_) |- a [Ax]
     * [i + 4] Г, a, a -> b, a -> (b -> _|_) |- b [E->]
     * [i + 3] Г, a, a -> b, a -> (b -> _|_) |- _|_ [E->]
     * [i + 2] Г, a -> b, a -> (b -> _|_) |- a -> _|_ [I->]
     * [i + 1] Г, a -> b |- (a -> (b -> _|_)) -> (a -> _|_) [I->]
     * [i] Г |- (a -> b) -> (a -> (b -> _|_)) -> (a -> _|_) [I->]
     */
    public static List<String> axiom9(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        Expression ab = new Implication(a, b);
        Expression lie = new Lie();
        Expression notA = new Implication(a, lie);
        Expression notB = new Implication(b, lie);
        Expression aNotB = new Implication(a, notB);
        hypothesises.add(a);  hypothesises.add(ab);  hypothesises.add(aNotB);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, aNotB, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, a, notB, i + 4));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, ab, i + 5));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 5));
        result.add(BasicOutputs.implicationOut(hypothesises, a, b, i + 4));
        result.add(BasicOutputs.implicationOut(hypothesises, b, lie, i + 3));
        result.add(BasicOutputs.implicationIn(hypothesises, a, lie, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, aNotB, notA, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, ab, new Implication(aNotB, notA), i));
        return result;
    }

    /**
     *  [i + 4] Г, a, (a -> _|_) |- (a -> _|_) [Ax]
     *  [i + 4] Г, a, (a -> _|_) |- a [Ax]
     *  [i + 3] Г, a, (a -> _|_) |- _|_ [E->]
     *  [i + 2] Г, a, (a -> _|_) |- b [E_|_]
     *  [i + 1] Г, a |- (a -> _|_) -> b [I->]
     *  [i] Г |- a -> (a -> _|_) -> b [I->]
     */
    public static List<String> axiom10(List<Expression> hypothesises, Expression a, Expression b, int i) {
        List<String> result = new ArrayList<>();
        Expression lie = new Lie();
        Expression notA = new Implication(a, lie);
        hypothesises.add(a); hypothesises.add(notA);
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, notA, i + 4));
        result.add(BasicOutputs.takeFromHypothesis(hypothesises, a, i + 4));
        result.add(BasicOutputs.implicationOut(hypothesises, a, lie, i + 3));
        result.add(BasicOutputs.notOut(hypothesises, b, i + 2));
        result.add(BasicOutputs.implicationIn(hypothesises, notA, b, i + 1));
        result.add(BasicOutputs.implicationIn(hypothesises, a, new Implication(notA, b), i));
        return result;
    }
}
