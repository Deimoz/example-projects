package expression.parser;

import expression.exceptions.*;
import expression.expressions.*;
import expression.generic.NumberType;

import java.util.List;

// :NOTE: any bounds on `E`?
public class ExpressionParser<E extends Number> implements Parser<E> {
    private NumberType<E> opType;

    public ExpressionParser(NumberType<E> opType) {
        this.opType = opType;
    }

    private CommonExpression<E> parse(StringSource expression) throws ParseExpressionException {
        // :NOTE: where is parameterization?
        return new LocalParser<>(expression, opType).parse();
    }

    @Override
    public CommonExpression<E> parse(String expression) throws ParseExpressionException {
        return parse(new StringSource(expression));
    }

    private static class LocalParser<E extends Number> extends BaseParser<E> {
        private int balance;

        public LocalParser(ExpressionSource source, NumberType<E> opType) {
            super(source, opType);
        }

        public CommonExpression<E> parse() throws ParseExpressionException {
            balance = 0;
            CommonExpression<E> result = parseObj(0, false);
            expectEnd();
            return result;
        }

        private CommonExpression<E> parseObj(int pr, boolean withUnaryMinus) throws ParseExpressionException {
            CommonExpression<E> leftElement = parseValue(false);
            while (true) {
                skipWhitespace();

                if (pr < 1 && test('m')) {
                    if (test("in")) {
                        leftElement = new Min<E>(leftElement, parseObj(1, withUnaryMinus), opType);
                    } else if (test("ax")) {
                        leftElement = new Max<E>(leftElement, parseObj(1, withUnaryMinus), opType);
                    } else {
                        throw new IllegalSymbolException("Wrong max or min name on position " + positionInfo());
                    }
                } else if (pr < 2 && test('-')) {
                    leftElement = new Subtract<E>(leftElement, parseObj(2, withUnaryMinus), opType);
                } else if (pr < 2 && test('+')) {
                    leftElement = new Add<E>(leftElement, parseObj(2, withUnaryMinus), opType);
                }  else if (pr < 3 && test('*')) {
                    leftElement = new Multiply<E>(leftElement, parseValue(false), opType);
                } else if (pr < 3 && test('/')) {
                    leftElement = new Divide<E>(leftElement, parseValue(false), opType);
                } else {
                    if (ch == ')' && balance == 0) {
                        throw new MissingBracketException("Missing open bracket on position " + positionInfo());
                    }
                    return leftElement;
                }
            }
        }

        private boolean notPossibleAfterOperator() {
            return !Character.isWhitespace(ch) && ch != '(' && ch != '-';
        }

        private boolean operator() {
            return ch == '+' || ch == '*' || ch == '/' || ch == '-' || ch == ')' || ch == '(' || ch == '\0';
        }

        private CommonExpression<E> parseValue(boolean withUnaryMinus) throws ParseExpressionException {
            skipWhitespace();
            if (test('(')) {
                balance++;
                CommonExpression<E> temp = parseObj(0, withUnaryMinus);
                if (test(')')) {
                    balance--;
                    if (withUnaryMinus) return new Negate<E>(temp, opType);
                    return temp;
                }
                throw new MissingBracketException("Missing close bracket on position " + positionInfo());
            } else if (test('-')) {
                return  parseValue(!withUnaryMinus);
            } else if (test("count")) {
                if (notPossibleAfterOperator()) {
                    throw new IllegalSymbolException("Wrong 'count' name on position " + positionInfo());
                }
                // :NOTE: where is parameterization here?!
                CommonExpression<E> temp = new Count<E>(parseValue(withUnaryMinus), opType);
                if (withUnaryMinus) return new Negate<E>(temp, opType);
                return temp;
            } else {
                 List<Character> variableNames = List.of('x', 'y', 'z');
                 for (char v : variableNames) {
                     if (test(v)) {
                         // :NOTE: where is parameterization?
                         Variable temp = new Variable(String.valueOf(v));
                         if (withUnaryMinus) return new Negate<E>(temp, opType);
                         // :NOTE: unchecked cast?
                         return temp;
                     }
                 }
                 if (Character.isLetter(ch)) {
                     throw new IllegalSymbolException("Wrong variable name on position " + positionInfo());
                 }
                 if (operator()) {
                     throw new MissingArgumentException("Missing argument on position " + positionInfo());
                 }
                 if (Character.isDigit(ch)) {
                     // :NOTE: unchecked cast?
                     return parseNumber(withUnaryMinus);
                 }
                 throw new IllegalSymbolException("Wrong argument on position " + positionInfo());
            }
        }
    }
}
