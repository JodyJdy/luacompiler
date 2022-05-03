package com.jdy.lua.lex;

import java.util.Arrays;
import java.util.List;

/**
 * 枚举所有的token
 */
public enum TokenEnum {

    ADD('+'),SUB('-'),MUL('*'),DIV('/'),MOD('%'),GT('>'),
    LT('<'), BITXOR('~'),BITOR('|'),BITAND('&'),POW('^'),
    COLON(':'),LEN('#'),
    ASSIGN('='),BIG_LEFT('{'),BIG_RIGHT('}'),MID_LEFT('['),
    MID_RIGHT(']'),SMALL_LEFT('('),SMALL_RIGHT(')'),DOT('.'),
    DOUBLE_MARK('"'),SINGLE_MARK('\''),COMMA(','),SEMICON(';'),

    AND("and"),BREAK("break"),DO("do"),ELSE("else"),ELSEIF("elseif"),
    END("end"),FALSE("false"),FOR("for"),FUNCTION("function"),GOTO("goto"),
    IF("if"),IN("in"),LOCAL("local"),NIL("nil"),NOT("not"),OR("or"),REPEAT("repeat"),
            RETURN("return"),THEN("then"),TRUE("true"),UNTIL("until"),WHILE("while"),IDIV("//"),
            CAT(".."),VARARG("..."),EQ("=="),GE(">="),LE("<="),NE("~="),
            LSHIFT("<<"),RSHIFT(">>"),DOU_COLON("::"),EOF("<eof>"),
            FLOAT("<number>"),INT("<integer>"),NAME("<name>"),STRING("<string>")
   ;

    private Character singleChar;
    private String str;
    /**
     * 保留词汇
     */
    private static List<TokenEnum> reserved = Arrays.asList(AND,BREAK,DO,ELSE,ELSEIF,END,FALSE,FOR
        ,FUNCTION,GOTO,IF,IN,LOCAL,NIL,NOT,OR,REPEAT,RETURN,THEN,UNTIL,WHILE,TRUE);


    TokenEnum(char ch){
        this.singleChar = ch;
    }
    TokenEnum(String str){
        this.str = str;
    }
    public static TokenEnum getTokenEnum(char ch){
        for(TokenEnum e : TokenEnum.values()){
            if(e.singleChar !=null && e.singleChar == ch){
                return e;
            }
        }
        return null;
    }
    public static TokenEnum getTokenEnum(String str){
        for(TokenEnum e: TokenEnum.values()){
            if(e.str != null && e.str.equals(str)){
                return e;
            }
        }
        return null;
    }

    /**
     * 判断是不是保留 词汇
     */
    public static TokenEnum reservedToken(String str){
        for(TokenEnum e : reserved){
            if(str.equals(e.str)){
                return e;
            }
        }
        return null;
    }

    public Character getSingleChar() {
        return singleChar;
    }

    public String getStr() {
        return str;
    }
}
