package com.jdy.lua.lex;

import com.jdy.lua.lobjects.Table;
import com.jdy.lua.lparser.DynData;
import com.jdy.lua.lparser.FuncState;
import com.jdy.lua.lstate.LuaState;
import lombok.Data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
public class LexState {

    /**
     * 当前读取的 字符
     */
    int current;
    /**
     * 当前的行号
     */
    int linenumber;
    /**
     * 上一个 token的行号
     */
    int lastline;
    /**
     * 当前的token
     */
    Token t;
    /**
     * 向前看一个token
     */
    Token lookahead;
    FuncState fs;
    LuaState L;
    Table h = new Table();  /* to avoid collection/reuse strings  用于收集字符串 */
    /**
     * 当前parser使用到的数据
     */
    DynData dyd;
    /**
     * 当前source的名称
     */
    String source;
    /**
     * 当前 env的名称
     */
    String envn;

    InputStream reader;
    /**
     * 作为buffer使用
     */
    List<Integer> buffer = new ArrayList<>();

    public TokenEnum getCurTokenEnum(){
        return t.getToken();
    }
    TokenEnum getNextTokenEnum(){
        return lookahead.getToken();
    }
    void setCurTokenEnum(TokenEnum e){
        t.setToken(e);
    }
    void setNextTokenEnum(TokenEnum e){
        lookahead.setToken(e);
    }
}
