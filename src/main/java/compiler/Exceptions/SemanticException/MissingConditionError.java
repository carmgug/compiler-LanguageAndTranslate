package compiler.Exceptions.SemanticException;

public class MissingConditionError extends SemanticException {

    public MissingConditionError(String message) {
        super("MissingConditionError: " + message);
    }
}
