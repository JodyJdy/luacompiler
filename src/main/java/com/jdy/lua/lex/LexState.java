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

    int current;  /* current character (charint) */
    int linenumber;  /* input line counter */
    int lastline;  /* line of last token 'consumed' */
    Token t;  /* current token */
    Token lookahead;  /* look ahead token  向前看的 token */
    FuncState fs;  /* current function (parser) */
    LuaState L;
    Table h;  /* to avoid collection/reuse strings  用于收集字符串 */
    DynData dyd;  /* dynamic structures used by the parser */
    String source;  /* current source name */
    String envn;  /* environment variable name */

    InputStream reader;
    /**
     * 作为buffer使用
     */
    List<Integer> buffer = new ArrayList<>();
}
