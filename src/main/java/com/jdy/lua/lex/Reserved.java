package com.jdy.lua.lex;

import lombok.Data;
import lombok.Getter;

import static com.jdy.lua.lex.LexContants.FIRST_RESERVED;
/**
 * 保留词
 */
@Getter
public enum  Reserved {


    /* terminal symbols denoted by reserved words */
    TK_AND(FIRST_RESERVED), TK_BREAK(FIRST_RESERVED + 1),
    TK_DO(FIRST_RESERVED + 2), TK_ELSE(FIRST_RESERVED + 3), TK_ELSEIF(FIRST_RESERVED + 4), TK_END(FIRST_RESERVED + 5),
    TK_FALSE(FIRST_RESERVED + 6), TK_FOR(FIRST_RESERVED + 7), TK_FUNCTION(FIRST_RESERVED + 8),
    TK_GOTO(FIRST_RESERVED + 9), TK_IF(FIRST_RESERVED + 10), TK_IN(FIRST_RESERVED + 11), TK_LOCAL(FIRST_RESERVED + 12),
    TK_NIL(FIRST_RESERVED + 13), TK_NOT(FIRST_RESERVED + 14), TK_OR(FIRST_RESERVED + 15), TK_REPEAT(FIRST_RESERVED + 16),
    TK_RETURN(FIRST_RESERVED + 17), TK_THEN(FIRST_RESERVED + 18), TK_TRUE(FIRST_RESERVED + 19), TK_UNTIL(FIRST_RESERVED + 20),
    TK_WHILE(FIRST_RESERVED + 21),
    /* other terminal symbols */
    TK_IDIV(FIRST_RESERVED + 22), TK_CONCAT(FIRST_RESERVED + 23), TK_DOTS(FIRST_RESERVED + 24), TK_EQ(FIRST_RESERVED + 25),
    TK_GE(FIRST_RESERVED + 26), TK_LE(FIRST_RESERVED + 27), TK_NE(FIRST_RESERVED + 28),
    TK_SHL(FIRST_RESERVED + 29), TK_SHR(FIRST_RESERVED + 30),
    TK_DBCOLON(FIRST_RESERVED + 31), TK_EOS(FIRST_RESERVED + 32),
    TK_FLT(FIRST_RESERVED + 33), TK_INT(FIRST_RESERVED + 34), TK_NAME(FIRST_RESERVED + 35), TK_STRING(FIRST_RESERVED + 36);


    int t;
    Reserved(int t){
        this.t = t;
    }

    public static Reserved getReserved(int t){
        for(Reserved r : Reserved.values()){
            if(r.t == t){
                return r;
            }
        }
        return null;
    }

    public static Reserved isReserved(String t){
        for(Reserved r : Reserved.values()){
            int index = r.t - FIRST_RESERVED;
            //while是枚举类中最后一个保留词
            if(r.t <= TK_WHILE.t && Lex.luaX_tokens[index].equals(t)){
                return r;
            }
        }
        return null;
    }
}

