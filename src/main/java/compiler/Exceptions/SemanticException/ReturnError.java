package compiler.Exceptions.SemanticException;

public class ReturnError extends SemanticException {

    public ReturnError(String message) {
        super("ReturnError: " + message);
    }
}
