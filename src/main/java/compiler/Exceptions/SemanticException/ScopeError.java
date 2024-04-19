package compiler.Exceptions.SemanticException;

public class ScopeError extends SemanticException {
    public ScopeError(String message) {
        super("ScopeError: " + message);
    }
}
