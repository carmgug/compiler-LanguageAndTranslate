package compiler.Parser.AST;

public abstract class ASTNode {
    private ASTNode next;

    public ASTNode() {
    }

    public ASTNode getNext() {
        return next;
    }

    public void setNext(ASTNode next) {
        this.next = next;
    }
}
