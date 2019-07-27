package cn.seecoder;

import java.util.ArrayList;

// construct the AST tree
public class Parser {
    private Lexer lexer;

    public Parser(Lexer lexer){
        this.lexer=lexer;
    }

    public AST parse(){
        AST result=term(new ArrayList<String>());  //view it as a term at the beginning
        return result;
    }

    // term ::= LAMBDA LCID DOT term
    //        | application

    // resolve Abstraction(AST)
    public AST term(ArrayList<String> ctx){
        if(lexer.skip(Token.Type.LAMBDA)){
            Token id=lexer.token(Token.Type.LCID);
            lexer.match(Token.Type.DOT);
            ArrayList<String> temp=new ArrayList<String>(ctx);
            temp.add(0, id.getActual());
            // System.out.println(temp);
            AST term=term(temp);
            return new Abstraction(id.getActual(), term);
        }else{
            return application(ctx);
        }
    }


    //Application' ::= Atom Application'
    //               | ε  # empty

    // resolve Application(AST)
    public AST application(ArrayList<String> ctx){
        AST left=atom(ctx);
        while(true){
            AST right=atom(ctx);
            if(right==null){ //the way to terminate infinite recursion
                return left; 
            }else{
                left=new Application(left, right);
            }
        }
    }

    
    // atom ::= LPAREN term RPAREN
    //        | LCID

    // resolve Identifier(AST)
    public AST atom(ArrayList<String> ctx){
        if(lexer.skip(Token.Type.LPAREN)){                          
            AST term=term(ctx);
            lexer.match(Token.Type.RPAREN);
            return term;
        }else if(lexer.next(Token.Type.LCID)){
            Token id=lexer.token(Token.Type.LCID);
            int index=-1;
            for(String s:ctx)
                if(s.equals(id.getActual()))
                    index=ctx.indexOf(s);
            return new Identifier(Integer.toString(index));
        }else{
            return null;
        }
    }
}
