package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class WhileStatement extends ASTNode {

    private ExpressionStatement exitCondition;

    private Block block;

    public WhileStatement(ExpressionStatement exitCondition, Block block){
        this.exitCondition=exitCondition;
        this.block=block;
    }


    @Override
    public String toString() {
        return "WhileStatement : {" +
                "exitCondition=" + exitCondition +
                ", block:{" + block +
                "}}";
    }

    public ExpressionStatement getExitCondition() {
        return exitCondition;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable);
    }
}
