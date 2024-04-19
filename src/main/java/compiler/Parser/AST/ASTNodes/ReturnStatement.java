package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;

public class ReturnStatement extends ASTNode {

    private final ExpressionStatement expression;

    public ReturnStatement(){//return ;
        this.expression=null;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
    }

    @Override
    public Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticErrorException {
        throw new SemanticErrorException("Sould not run");
    }

    public ExpressionStatement gerExpression(){
        return expression;
    }

    public ReturnStatement(ExpressionStatement expression){//return expression;
        this.expression=expression;
    }

    public String toString(){
        if(expression==null){
            return "Return : void";
        }
        return "Return : { return_value_of: "+expression+"}";
    }

    public ExpressionStatement getExpression(){return  this.expression;}
}
