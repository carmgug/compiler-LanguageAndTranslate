package compiler.SemanticAnalysis;

import compiler.Exceptions.SemanticException.TypeErrorException;
import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.ExpressionStatement;
import compiler.Parser.AST.ASTNodes.Expressions.ArrayValueDeclaration;
import compiler.Parser.AST.ASTNodes.Expressions.BinaryExpression;
import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.Parser.AST.ASTNodes.Expressions.Types.ArrayType;
import compiler.Parser.AST.ASTNodes.Expressions.Value;

import java.util.*;

public class SymbolTable{

    protected SymbolTable previousTable; //link to previous table
    protected Map<String,SemanticType> entries;

    public SymbolTable(SymbolTable previousTable){
        this.previousTable=previousTable;
        this.entries=new HashMap<String, SemanticType>();
    }

    public SymbolTable(){
        this.previousTable=null;
        this.entries=new LinkedHashMap<String,SemanticType>();
    }

    public SymbolTable(Map<String,SemanticType> entries){
        this.previousTable=null;
        this.entries=entries;
    }

    public SymbolTable(SymbolTable previousTable, Map<String,SemanticType> entries){
        this.previousTable=previousTable;
        this.entries=entries;
    }

    public void add(String s, SemanticType type){
        this.entries.put(s,type);
    }

    public SemanticType get(String s){
        return this.entries.get(s);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Map.Entry<String, SemanticType> entry : entries.entrySet()) {
            sb.append("\t").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
    public SymbolTable getPreviousTable() {
        return previousTable;
    }

    public Map<String, SemanticType> getEntries() {
        return entries;
    }
}
