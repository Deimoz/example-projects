package expression.exceptions;

public class MissingArgumentException extends ParseExpressionException {
    public MissingArgumentException(String message) {
        super(message);
    }
}
