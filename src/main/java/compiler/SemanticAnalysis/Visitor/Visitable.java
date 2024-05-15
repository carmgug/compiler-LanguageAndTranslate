package compiler.SemanticAnalysis.Visitor;


import compiler.CodeGenerator.CodeGenerationVisitor;
import compiler.CodeGenerator.EvaluateVisitor;
import compiler.CodeGenerator.ScopesTable;
import compiler.Exceptions.SemanticException.SemanticException;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import org.objectweb.asm.MethodVisitor;

/*
    The visitable elements interface: This interface defines an accept method that takes the visitor as an argument.
    Each visitable element class will implement this interface.
 */
public interface Visitable {

    void accept(Visitor visitor, SymbolTable symbolTable,SymbolTable structTable) throws SemanticException;
    Type accept(VisitorType visitor, SymbolTable symbolTable, SymbolTable structTable) throws SemanticException;

    void accept(CodeGenerationVisitor codeGenerationVisitor, ScopesTable curr_scope, MethodVisitor mw);

    void accept(EvaluateVisitor visitor, ScopesTable curr_scope, MethodVisitor mw);
}
