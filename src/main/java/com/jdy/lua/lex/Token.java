package com.jdy.lua.lex;

import lombok.Data;

@Data
public class Token {

    /**
     * token类型值
     */
    TokenEnum token;
    double r;
    long i;
    String s;

    public Token(){

    }
    public Token(TokenEnum e){
        this.token = e;
    }
}
