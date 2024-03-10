package compiler.Parser.AST;

public class AbstractSyntaxTree{
    private ASTNode root;

    private ASTNode last;
    private int size;


    public AbstractSyntaxTree(){
        this.root=null;
        this.last=null;
        this.size=0;
    }
    public AbstractSyntaxTree(ASTNode root) {
        this.root = root;
        this.last = root;
        this.size=1;
    }


    public ASTNode getRoot() {
        return root;
    }

    public void setRoot(ASTNode root) {
        this.root = root;
    }

    public void addNode(ASTNode node){
        //In this case the AST is empty
        if(root==null){
            root=node;
            last=node;
            size++;
            return;
        }
        //In this case the AST has at least one node
        last.setNext(node);
        last=node;
        size++;
    }

    public int remove(){
        if(this.size==1){
            this.last=null;
            this.root=null;
            this.size=0;
        }else{

        }

        return 1;

    }
}
