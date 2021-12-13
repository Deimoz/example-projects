package parser;

import parser.expression.*;

public class Parser {

    public Expression parse(String s) {
        s = s.replaceAll("\\s", "").replace("\n", "");
        return tryImplication(s, 0).parsed;
    }

    private ParsingProcess tryImplication(String s, int curr) {
        ParsingProcess current = tryOr(s, curr);
        Expression parsed = current.parsed;
        while (current.rest < s.length()) {
            if (s.charAt(current.rest) != '-') {
                break;
            }
            current = tryImplication(s, current.rest + 2);
            parsed = new Implication(parsed, current.parsed);
        }
        return new ParsingProcess(parsed, current.rest);
    }

    private ParsingProcess tryOr(String s, int curr) {
        ParsingProcess current = tryAnd(s, curr);
        Expression parsed = current.parsed;
        while (current.rest < s.length()) {
            if (s.charAt(current.rest) != '|') {
                break;
            }
            current = tryAnd(s, current.rest + 1);
            parsed = new Or(parsed, current.parsed);
        }
        return new ParsingProcess(parsed, current.rest);
    }

    private ParsingProcess tryAnd(String s, int curr) {
        ParsingProcess current = tryBracket(s, curr);
        Expression parsed = current.parsed;
        while (true) {
            if (current.rest == s.length()) {
                return current;
            }
            if (s.charAt(current.rest) != '&') {
                return current;
            }
            ParsingProcess right = tryBracket(s, current.rest + 1);
            parsed = new And(parsed, right.parsed);
            current = new ParsingProcess(parsed, right.rest);
        }
    }

    private ParsingProcess tryBracket(String s, int curr) {
        if (s.charAt(curr) == '!') {
            ParsingProcess next = tryBracket(s, curr + 1);
            return new ParsingProcess(new Not(next.parsed), next.rest);
        }
        if (s.charAt(curr) == '(') {
            ParsingProcess r = tryImplication(s, curr + 1);
            if (s.charAt(r.rest) == ')') {
                r.rest++;
            }
            return r;
        }
        return tryVariable(s, curr);
    }

    private ParsingProcess tryVariable(String s, int curr) {
        StringBuilder curVar = new StringBuilder();
        int last = curr;
        for (int i = curr; i < s.length(); i++) {
            if (!(Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)) || s.charAt(i) == '\'')) {
                break;
            }
            curVar.append(s.charAt(i));
            last = i + 1;
        }
        return new ParsingProcess(new Variable(curVar.toString()), last);
    }
}