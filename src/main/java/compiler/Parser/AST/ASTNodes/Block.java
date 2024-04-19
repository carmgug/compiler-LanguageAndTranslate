package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

import java.util.ArrayList;
import java.util.Arrays;

public class Block extends ASTNode {

    private ArrayList<ASTNode> statements;

    public Block(ArrayList<ASTNode> statements){
        this.statements= new ArrayList<>(statements);

    }

    public String toString(){
        if(statements.isEmpty())
            return "Block : None";
        return "Block : "+ Arrays.toString(statements.toArray());
    }

    public ArrayList<ASTNode> getStatements(){
        return this.statements;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        throw new SemanticException("Sould not run");
    }

}
