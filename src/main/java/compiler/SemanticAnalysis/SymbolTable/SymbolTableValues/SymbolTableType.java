package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;


import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

public class SymbolTableType extends SymbolTableEntry {

    private Type type;

    public SymbolTableType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    public String toString(){
        return type.toString();
    }

}
