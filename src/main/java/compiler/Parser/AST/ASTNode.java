package compiler.Parser.AST;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public abstract class ASTNode implements Visitable {
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

    public abstract void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

}
