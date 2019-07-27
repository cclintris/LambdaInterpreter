package cn.seecoder;

public class Application implements AST{
    AST left;
    AST right; 

    public Application(AST left, AST right){
        this.left=left;
        this.right=right;
    }

    public String toString(){
        return "("+left.toString()+" "+right.toString()+")";
    }

}
