package compiler.Exceptions.SemanticException;

public class TypeError extends SemanticException{

    public TypeError(String message) {
        super("TypeError: "+message);
    }
}
