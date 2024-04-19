package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class WhileStatement extends ASTNode {

    private final ExpressionStatement exitCondition;

    private final Block block;
    private final int line;

    public WhileStatement(ExpressionStatement exitCondition, Block block, int line){
        this.exitCondition=exitCondition;
        this.block=block;
        this.line=line;
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
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }
    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        throw new SemanticErrorException("Sould not run");
    }


    public int getLine() {
        return line;
    }
}
