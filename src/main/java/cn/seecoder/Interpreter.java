package cn.seecoder;

public class Interpreter {

    boolean validInput = true;
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        parser = p;
        try{
            astAfterParser=p.parse();
        }catch(RuntimeException ex){
            ex.printStackTrace();
            System.out.println("invalid input");
            validInput = false;
        }
    }

    private boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }

    public AST eval(){
        return evalAST(astAfterParser);
    }

    // check AST type and calculate
    private AST evalAST(AST ast){
        while(true){
            if(isApplication(ast)){
                if(isApplication(((Application)ast).left)){
                    ((Application)ast).left=evalAST(((Application)ast).left);
                    if(isApplication(((Application)ast).left))
                        return ast;
                }
                else if(isAbstraction(((Application)ast).left)){
                    if(isApplication(((Application)ast).right)) 
                        ((Application)ast).right=evalAST(((Application)ast).right);
                    ast=substitute(((Abstraction)((Application)ast).left).body, ((Application)ast).right);
                }
                else{
                    if(isApplication(((Application)ast).right)){
                        ((Application)ast).right=evalAST(((Application)ast).right);
                        return ast;
                    }else if(isAbstraction(((Application)ast).right)){
                        ((Application)ast).right=evalAST(((Application)ast).right);
                        return ast;
                    }
                    return ast;
                }
            }else if(isAbstraction(ast)){
                ((Abstraction)ast).body=evalAST(((Abstraction)ast).body);
                return ast;
            }else{
                return ast;
            }
        }
    }

    private AST substitute(AST node,AST value){
        return shift(-1,subst(node, shift(1,value,0) ,0),0);  // inner shift simplify rhs
    }

    
    private AST subst(AST node, AST value, int depth){ //depth 相當於計數
        if(isIdentifier(node)){
            if(Integer.valueOf(((Identifier)node).value)==depth)
                return shift(depth, value, 0);
            else
                return node;
        }else if(isApplication(node)){
            return new Application(subst(((Application)node).left, value, depth), subst(((Application)node).right, value, depth));
        }else if(isAbstraction(node)){
            return new Abstraction(((Abstraction)node).param, subst(((Abstraction)node).body, value, depth+1));
        }
        return null;
    }


    // from: 內層深度 by: 位移的距離  
    private AST shift(int by, AST node,int from){
        if(isIdentifier(node)){
            if(((Identifier)node).value.equals("-1"))
                return node;
            int temp=Integer.valueOf(((Identifier)node).value);
            temp+=(temp>=from?by:0);
            return new Identifier(((Identifier)node).name, String.valueOf(temp));
        }else if(isApplication(node)){
            return new Application(shift(by, ((Application)node).left, from), shift(by, ((Application)node).right, from));
        }else if(isAbstraction(node)){
            return new Abstraction(((Abstraction)node).param, shift(by, ((Abstraction)node).body, from+1));
        }
        return null;
    }



    
    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.m "+SUCC+" n)";
    static String POW = "(\\b.\\e.e b)";      
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    public static String app(String func, String x){
        return "(" + func + x + ")";
    }
    public static String app(String func, String x, String y){
        return "((" + func + x + ")" + y + ")";
    }
    public static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        String[] sources = {
            ZERO,//0
            ONE,//1
            TWO,//2
            THREE,//3
            app(PLUS, ZERO, ONE),//4
            app(PLUS, TWO, THREE),//5
            app(POW, TWO, TWO),//6
            app(PRED, ONE),//7
            app(PRED, TWO),//8
            app(SUB, FOUR, TWO),//9
            app(AND, TRUE, TRUE),//10
            app(AND, TRUE, FALSE),//11
            app(AND, FALSE, FALSE),//12
            app(OR, TRUE, TRUE),//13
            app(OR, TRUE, FALSE),//14
            app(OR, FALSE, FALSE),//15
            app(NOT, TRUE),//16
            app(NOT, FALSE),//17
            app(IF, TRUE, TRUE, FALSE),//18
            app(IF, FALSE, TRUE, FALSE),//19
            app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
            app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
            app(ISZERO, ZERO),//22
            app(ISZERO, ONE),//23
            app(LEQ, THREE, TWO),//24
            app(LEQ, TWO, THREE),//25
            app(EQ, TWO, FOUR),//26
            app(EQ, FIVE, FIVE),//27
            app(MAX, ONE, TWO),//28
            app(MAX, FOUR, TWO),//29
            app(MIN, ONE, TWO),//30
            app(MIN, FOUR, TWO),//31
            "\\x x",// invalid input 32
        };

        int i = 22;
    
        String source = sources[i];

        Lexer lexer = new Lexer(source);
    
        Parser parser = new Parser(lexer);
    
        Interpreter interpreter = new Interpreter(parser);
        
        if(interpreter.validInput){
            AST result = interpreter.eval();
            System.out.println("result: " + result.toString());
        }    
        // System.out.println("end");
    }
}
