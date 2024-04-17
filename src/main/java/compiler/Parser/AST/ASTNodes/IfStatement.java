package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

import java.beans.Expression;

public class IfStatement extends ASTNode {
    protected ExpressionStatement ifCondition;
    protected Block ifBlock;

    public IfStatement(ExpressionStatement ifCondition, Block ifBlock){
        this.ifCondition=ifCondition;
        this.ifBlock=ifBlock;
    }

    public IfStatement(){
        this.ifCondition=null;
        this.ifBlock=null;
    }

    public String toString(){
        return "IfStatement : {" +
                "ifCondition : " + ifCondition + "," +
                "ifBlock : {" + ifBlock + "}}";

    }

    public ExpressionStatement getIfCondition() {
        return ifCondition;
    }

    public Block getIfBlock() {
        return ifBlock;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
}
