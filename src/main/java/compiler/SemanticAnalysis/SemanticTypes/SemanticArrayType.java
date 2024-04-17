package compiler.SemanticAnalysis.SemanticTypes;

import compiler.SemanticAnalysis.SemanticType;

public class SemanticArrayType extends SemanticType {

    int size;


    public SemanticArrayType(String type,int size) {
        super(type);
        this.size=size;
    }


    public int getSize() {
        return size;
    }
}
