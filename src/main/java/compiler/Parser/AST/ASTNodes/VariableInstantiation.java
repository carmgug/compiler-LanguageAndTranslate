package compiler.Parser.AST.ASTNodes;

import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.VariableReference;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;

public class VariableInstantiation  extends VariableDeclaration {

    protected ExpressionStatement right_side;

    public VariableInstantiation(Type type, VariableReference variableReference, ExpressionStatement right_side){
        super(type,variableReference);
        this.right_side=right_side;
    }

    public String toString(){
        return "VariableInstantiation: {"+
                "Type: " + type + ","+
                "Identifier: " + variable.getIdentifier() + ","+
                "Right side: " + right_side +
                "}";
    }

    public ExpressionStatement getRight_side() {
        return right_side;
    }

    public void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        visitor.visit(this,symbolTable,structTable);
    }

}
