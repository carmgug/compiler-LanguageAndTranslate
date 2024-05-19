package compiler.Exceptions.SemanticException;

public class ArgumentError extends SemanticException {

        public ArgumentError(String message) {
            super("ArgumentError: " + message);
        }
}
