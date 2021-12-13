package naturalOut;

import parser.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicOutputs {

    public static String hypToString(List<Expression> hypothesises) {
        return hypothesises.stream().map(Expression::toString).collect(Collectors.joining(","));
    }

    private static String getIndex(int index) {
        return "[" + index + "] ";
    }

    /**
     * Ax
     * Г, a |- a, a should be in Г already
     */
    public static String takeFromHypothesis(List<Expression> hypothesises, Expression a, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + a.toString() + " [Ax]";
    }

    /**
     * I->
     * Г |- b, Г, a |- a - should be proofed before
     * Г |- a -> b (I->)
     */
    public static String implicationIn(List<Expression> hypothesises, Expression left, Expression right, int index) {
        hypothesises.remove(left);
        return getIndex(index) + hypToString(hypothesises) + "|-" + left.toString() + "->" + right.toString() + " [I->]";
    }

    /**
     * E->
     * Г |- a -> b, Г |- a - should be proofed before
     * Г |- b
     */
    public static String implicationOut(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + right.toString() + " [E->]";
    }

    /**
     * I&
     * Г |- b, Г |- a - should be proofed before
     * Г |- a & b
     */
    public static String andIn(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + left.toString() + "&" + right.toString() + " [I&]";
    }

    /**
     * El&
     * Г |- a & b - should be proofed before
     * Г |- a
     */
    public static String andOutLeft(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + left.toString() + " [El&]";
    }

    /**
     * Er&
     * Г |- a & b - should be proofed before
     * Г |- b
     */
    public static String andOutRight(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + right.toString() + " [Er&]";
    }

    /**
     * Il|
     * Г |- a - should be proofed before
     * Г |- a | b
     */
    public static String orInLeft(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + left.toString() + "|" + right.toString() + " [Il|]";
    }

    /**
     * Ir|
     * Г |- b - should be proofed before
     * Г |- a | b
     */
    public static String orInRight(List<Expression> hypothesises, Expression left, Expression right, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + left.toString() + "|" + right.toString() + " [Ir|]";
    }

    /**
     * E|
     * Г, a |- c, Г, b |- c, Г |- a | b - should be proofed before
     * Г |- c
     */
    public static String orOut(List<Expression> hypothesises, Expression left, Expression right, Expression p, int index) {
        hypothesises.remove(left);
        hypothesises.remove(right);
        return getIndex(index) + hypToString(hypothesises) + "|-" + p.toString() + " [E|]";
    }

    /**
     * E_|_
     * Г |- _|_ - should be proofed
     * Г |- a
     */
    public static String notOut(List<Expression> hypothesises, Expression a, int index) {
        return getIndex(index) + hypToString(hypothesises) + "|-" + a.toString() + " [E_|_]";
    }
}
