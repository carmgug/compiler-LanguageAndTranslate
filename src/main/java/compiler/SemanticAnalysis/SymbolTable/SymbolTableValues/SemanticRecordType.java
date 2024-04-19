package compiler.SemanticAnalysis.SymbolTable.SymbolTableValues;

import compiler.Parser.AST.ASTNodes.Expressions.Type;
import compiler.SemanticAnalysis.SymbolTable.SymbolTableEntry;

import java.util.Map;

public class SemanticRecordType extends SymbolTableEntry {

    private Map<String, SymbolTableEntry> fields;
    private Type type;



    public SemanticRecordType(Type type, Map<String, SymbolTableEntry> fields) {
        this.type=type;
        this.fields=fields;
    }

    public Map<String, SymbolTableEntry> getFields(){return this.fields;}

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<String, SymbolTableEntry> entry : this.fields.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            String key = entry.getKey();
            SymbolTableEntry value = entry.getValue();

            sb.append(key).append("=").append(value != null ? value.toString() : "null");
        }

        sb.append("}");
        return sb.toString();
    }

}
