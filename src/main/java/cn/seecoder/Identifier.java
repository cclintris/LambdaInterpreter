package cn.seecoder;

public class Identifier implements AST{
    String name;
    String value; //De Bruijn index

    public Identifier(String value){
        this.value=value;
    }

    public Identifier(String name, String value){
        this.name=name;
        this.value=value;
    }

    public String toString(){
        return value;
    }
}
