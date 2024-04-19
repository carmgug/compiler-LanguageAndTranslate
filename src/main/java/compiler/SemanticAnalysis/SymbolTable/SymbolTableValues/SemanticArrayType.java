package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;

import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

public class SemanticArrayType extends SymbolTableEntry {

    private int size;
    private Type type;



    public SemanticArrayType(Type type,int size) {
        this.type=type;
        this.size=size;
    }


    public int getSize() {
        return size;
    }
}
