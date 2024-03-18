package compiler.Parser.AST;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.GlobalVariable;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;

public class Program implements AbstractSyntaxTree{
    ArrayList<Constant> costants;
    ArrayList<Struct> structs;

    ArrayList<GlobalVariable> global_variables;
    ArrayList<Procedure> procedures;

    public Program(){
        this.costants=new ArrayList<Constant>();
        this.structs=new ArrayList<Struct>();
        this.global_variables=new ArrayList<GlobalVariable>();
        this.procedures=new ArrayList<Procedure>();
    }




    public boolean addConstant(Constant c){
        return this.costants.add(c);
    }

    public boolean addStruct(Struct s){
        return this.structs.add(s);
    }

    public boolean addGlobalVariables(GlobalVariable g){
        return this.global_variables.add(g);
    }

    public boolean addProcedure(Procedure p){
        return this.procedures.add(p);
    }


    public ArrayList<Constant> getCostants() {
        return new ArrayList<>(costants);
    }

    public ArrayList<Procedure> getProcedures() {
        return new ArrayList<>(procedures);
    }

    public ArrayList<GlobalVariable> getGlobal_variables() {
        //Return a shallow copy of the ArrayList
        return new ArrayList<GlobalVariable>(global_variables);
    }

    public ArrayList<Struct> getStructs() {
        return new ArrayList<>(structs);
    }

    public String toString(){
        StringBuilder sb=new StringBuilder();
        for (Constant c : costants) {
            sb.append(c.toString());
        }
        return sb.toString();
    }





}
