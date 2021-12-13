package exceptions;

public class LogicException extends Exception {
    private final String message;

    public LogicException(String s) {
        message = s;
    }

    public String getMessage() {
        return message;
    }
}
