package expression.exceptions;

public class MissingBracketException extends ParseExpressionException {
    public MissingBracketException(String message) {
        super(message);
    }
}
