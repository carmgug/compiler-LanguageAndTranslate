package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;

import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTable;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class SemanticStructType extends SymbolTableEntry {


    private SymbolTable fields;
    private Type type;


    public SemanticStructType(Type type, SymbolTable fields) {
        this.type=type;
        this.fields=fields;
    }

    public SemanticStructType(Type type) {
        this.type=type;
        this.fields=new SymbolTable();
    }

    public SymbolTable getFields(){return this.fields;}

    public void addField(String variable_name, SymbolTableEntry type){
        this.fields.add(variable_name,type);
    }

    public Type getType(){
        return this.type;
    }


}
