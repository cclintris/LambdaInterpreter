package cn.seecoder;

import java.util.regex.Pattern;

public class Lexer {

    private String source;
    private int index;
    private Token token;

    public Lexer(String s){
        this.source=s;
        this.index=0;
        getToken();
    }

    public char getchar(){
        if(this.index>=this.source.length()){
            return '\0';
        }else{
            return this.source.charAt(index++);
        }
    }

    public void getToken(){
        char tempch;
        do{
            tempch=getchar();
        }while(Pattern.matches("\\s", String.valueOf(tempch)));
        switch (tempch){
            case '(':
                this.token=new Token(Token.Type.LPAREN, "(");
                break;
            case ')':
                this.token=new Token(Token.Type.RPAREN, ")");
                break;
            case '\\':
                this.token=new Token(Token.Type.LAMBDA, "\\");
                break;
            case '.':
                this.token=new Token(Token.Type.DOT, ".");
                break;
            case '\0':
                this.token=new Token(Token.Type.EOF, "\0");
                break;
            default:
                if(Pattern.matches("[a-z]", String.valueOf(tempch))){
                    StringBuilder tempstr=new StringBuilder();
                    do{
                        tempstr.append(tempch);
                        tempch=getchar();
                    }while(Pattern.matches("[a-zA-Z]", String.valueOf(tempch)));{
                        index--;
                        this.token=new Token(Token.Type.LCID, tempstr.toString());
                    }

                }
        }
        System.out.println(token.getType());
    }

    public boolean next(Token.Type type){
        return token.getType().equals(type);
    }

    public boolean skip(Token.Type type){
        if(next(type)){
            getToken();
            return true;
        }
        return false;
    }

    public void match(Token.Type type) throws RuntimeException{
        if(next(type)){
            getToken();
        }else{
            throw new RuntimeException("invalid input");
        }
    }
    
    public Token token(Token.Type type){
        Token temp=token;
        match(type);
        return temp;
    }


    //Test Lexer class code
    int test=0;
    private void display(){
        while(test<=source.length()){
            getToken();
            test++;
            System.out.println(token.getType()+":"+token.getActual());
        }
    }

    public static void main(String[] args){
        Lexer lexer=new Lexer("(\\x.\\y.x)(\\x.x)(\\y.y)");
        lexer.display();
    }


}
