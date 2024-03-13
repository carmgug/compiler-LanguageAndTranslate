package compiler.Parser.AST;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;

import java.util.ArrayList;

public class Program implements AbstractSyntaxTree{
    ArrayList<Constant> costants;
    ArrayList<Struct> structs;

    ArrayList<Integer> global_variables;
    ArrayList<Procedure> procedures;

    public Program(){
        this.costants=new ArrayList<Constant>();
        this.structs=new ArrayList<Struct>();

        this.global_variables=new ArrayList<Integer>();
        this.procedures=new ArrayList<Procedure>();
    }




    public boolean addConstant(Constant c){
        return this.costants.add(c);
    }

    public boolean addStruct(Struct s){
        return this.structs.add(s);
    }

    public boolean addGlobalVariables(Integer x){
        return this.global_variables.add(x);
    }

    public boolean addProcedure(Procedure p){
        return this.procedures.add(p);
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        for (Constant c : costants) {
            sb.append(c.toString());
        }
        return sb.toString();
    }



}
