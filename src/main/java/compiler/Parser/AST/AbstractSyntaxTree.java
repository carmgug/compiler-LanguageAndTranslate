package compiler.Parser.AST;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;

public interface AbstractSyntaxTree {


    /*
        @input Constant C
        @return boolean (True if c has added to constant as Collection.add)
     */
    boolean addConstant(Constant c);

    /*
        @input Struct S
        @return boolean (True if s has added successfully)
     */
    boolean addStruct(Struct s);
    /*
        @input Integer x
        @return boolean (True if x has added to global_variables as Collection.add)
     */
    boolean addGlobalVariables(Integer x);

    /*
        @input Procedure p
        @return boolean (True if p has added to procedures as Collection.add)
     */
    boolean addProcedure(Procedure p);

}
