package compiler.SemanticAnalysis;

public class SemanticType {

    String type;

    public SemanticType(String type){
        this.type=type;
    }

    public String getType(){
        return this.type;
    }
    @Override
    public String toString(){
        return type;
    }

}
