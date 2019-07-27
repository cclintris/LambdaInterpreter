package cn.seecoder;

public class Token {
  private Type type;
  private String actual; //In order to record and display the actual value of the tokens

  //define the limited types of tokens, use strcuture enum in order to Switch
  public enum Type{
    LPAREN, RPAREN, LAMBDA, DOT, LCID, EOF;
  }

  Token(Type type, String actual){
    this.type=type;
    this.actual=actual;
  }

  public Type getType(){
    return this.type;
  }

  public String getActual(){
    return this.actual;
  }

}
