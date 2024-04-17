package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticErrorException;
import compiler.Parser.AST.ASTNode;
import compiler.SemanticAnalysis.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class ReturnStatement extends ASTNode {

    private final ExpressionStatement expression;

    public ReturnStatement(){//return ;
        this.expression=null;
    }

    @Override
    public void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticErrorException {
        visitor.visit(this,symbolTable,structTable);
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
