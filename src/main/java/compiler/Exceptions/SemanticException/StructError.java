package compiler.Exceptions.SemanticException;

public class StructError extends SemanticException {

    public StructError(String message) {
        super("StructError: " + message);
    }
}
