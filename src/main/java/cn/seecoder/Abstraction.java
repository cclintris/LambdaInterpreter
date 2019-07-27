package cn.seecoder;

public class Abstraction implements AST{
    String param;
    AST body;

    public Abstraction(String param, AST body){
        this.param=param;
        this.body=body;
    }

    public String toString(){
        return "\\."+body.toString();
    }
}

