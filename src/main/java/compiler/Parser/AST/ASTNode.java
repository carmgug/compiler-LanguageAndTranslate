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


    protected String generateIndentation(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }
}
