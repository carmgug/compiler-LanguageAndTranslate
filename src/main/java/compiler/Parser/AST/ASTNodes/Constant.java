package compiler.Parser.AST.ASTNodes;


import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Lexer.Symbol;
import compiler.Parser.AST.ASTNode;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.Visitor.Visitor;
import compiler.SemanticAnalysis.Visitor.VisitorType;
import org.objectweb.asm.MethodVisitor;

public class Constant extends ASTNode {

    private Type type;
    private Symbol identifier;
    private ExpressionStatement right_side;

    public Constant(Type type, Symbol identifier, ExpressionStatement right_side) {
        this.type = type;
        this.identifier = identifier;
        this.right_side = right_side;
    }

    public Symbol getIdentifier(){
        return this.identifier;
    }
    public String getConstantName(){
        return this.identifier.getValue();
    }
    public int getLine(){
        return this.identifier.getLine();
    }

    public Type getType(){
        return this.type;
    }

    public ExpressionStatement getRight_side(){
        return this.right_side;
    }

    public void accept(Visitor visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        visitor.visit(this, symbolTable,structTable);
    }

    public Type accept(VisitorType visitorType, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException {
        return visitorType.visit(this, symbolTable,structTable);
    }

    public void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw){
        codeGenerationVisitor.visit(this, curr_scope, mw);
    }

    @Override
    public void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw) {
        throw new RuntimeException("Should not run!");
    }


    public String toString(){
        return "Constant: {"+
                "Type: " + type + ","+
                "Identifier :" + identifier.getValue() + ","+
                "Value: "+right_side +
                "}";
    }


}
