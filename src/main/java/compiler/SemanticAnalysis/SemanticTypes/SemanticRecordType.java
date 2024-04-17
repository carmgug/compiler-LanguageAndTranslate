package compiler.SemanticAnalysis.SemanticTypes;

import compiler.SemanticAnalysis.SemanticType;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SemanticRecordType extends SemanticType {

    Map<String,SemanticType> fields;



    public SemanticRecordType(String type, Map<String,SemanticType> fields) {
        super(type);
        this.fields=fields;
    }

    public Map<String,SemanticType> getFields(){return this.fields;}

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<String, SemanticType> entry : this.fields.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            String key = entry.getKey();
            SemanticType value = entry.getValue();

            sb.append(key).append("=").append(value != null ? value.toString() : "null");
        }

        sb.append("}");
        return sb.toString();
    }

}
