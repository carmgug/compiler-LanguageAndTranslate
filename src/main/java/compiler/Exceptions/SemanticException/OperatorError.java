package compiler.Exceptions.SemanticException;

public class OperatorError extends SemanticException {

        public OperatorError(String message) {
            super("OperatorError: " + message);
        }
}
