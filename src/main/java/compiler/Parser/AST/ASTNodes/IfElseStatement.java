package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class IfElseStatement extends IfStatement {

    protected Block else_block;

    public IfElseStatement(ExpressionStatement if_condition,Block if_block,Block else_block){
        super(if_condition,if_block);
        this.else_block=else_block;
    }

    public String toString(){
        return "IfElseStatement : {" +
                "ifCondition:" + ifCondition +
                ", ifBlock : {" + ifBlock + "}"+
                ", elseBlock : {" + else_block + "}"+
                '}';
    }

    public Block getElse_block() {
        return else_block;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        super.accept(visitor, symbolTable, structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        throw new SemanticErrorException("Sould not run");
    }
}
