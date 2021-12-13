package parser.expression;

public class ParsingProcess {

    public final Expression parsed;
    public int rest;

    public ParsingProcess(Expression parsed, int rest) {
        this.parsed = parsed;
        this.rest = rest;
    }
}