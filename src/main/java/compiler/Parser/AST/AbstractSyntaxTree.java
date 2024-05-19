package compiler.Parser.AST;

import compiler.Parser.AST.ASTNodes.Constant;
import compiler.Parser.AST.ASTNodes.GlobalVariable;
import compiler.Parser.AST.ASTNodes.Procedure;
import compiler.Parser.AST.ASTNodes.Struct;

public interface AbstractSyntaxTree {


    /*
        @input Constant C
        @return boolean (True if c has added to constant as Collection.add)
     */
    void add(ASTNode node);

}
