package com.jdy.lua.lex;

import com.jdy.lua.lctype.LCtype;
import com.jdy.lua.lobjects.TString;
import com.jdy.lua.lstate.LuaState;
import com.jdy.lua.lstring.LString;
import static com.jdy.lua.lex.LexContants.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static com.jdy.lua.lex.Reserved.*;

public class Lex {

    public static String LUA_ENV = "_ENV";

    /**
     * while 以及之前的字符串 保留到 字符串常量表中
     */
    public static int NUM_RESERVED = Reserved.values().length;

    /** 保留词 */
    static String luaX_tokens [] = {
        "and", "break", "do", "else", "elseif",
                "end", "false", "for", "function", "goto", "if",
                "in", "local", "nil", "not", "or", "repeat",
                "return", "then", "true", "until", "while",
                "//", "..", "...", "==", ">=", "<=", "~=",
                "<<", ">>", "::", "<eof>",
                "<number>", "<integer>", "<name>", "<string>"
    };

    void luaX_Init(LuaState luaState){
        TString e = LString.newStr(luaState,LUA_ENV);
        //gc部分代码，先不处理
        //处理保留的关键字
        for(int i=0; i < NUM_RESERVED;i++){
            TString s = LString.newStr(luaState,luaX_tokens[i]);
            s.setExtra(i + 1);
        }

    }

    void luaX_SetInput(LuaState l,LexState lexState,String source,int firstChar){
        lexState.t.token = 0;
        lexState.L = l;
        lexState.current = firstChar;
        lexState.lookahead.token = TK_EOS.t;
        lexState.fs = null;
        lexState.linenumber = 1;
        lexState.lastline = 1;
        lexState.source = source;
        lexState.envn = LString.newStr(lexState.L,LUA_ENV).getContents();

    }


    /**
     * 读取下一个字符
     */
    public static void next(LexState lexState){
        try {
            lexState.current = lexState.reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean currIsNewLine(LexState lexState){
        return lexState.current == '\n' || lexState.current == '\r';
    }
    public static void saveAndNext(LexState lexState){
        lexState.buffer.add(lexState.current);
        next(lexState);
    }
    public static void bufferRemove(LexState lexState,int n){
        List<Integer> lex = lexState.buffer;
        int i=0;
        while(i<n){
            lex.remove(lex.size() - 1);
            i++;
        }
    }
    public static void resetBuffer(LexState lexState){
        lexState.buffer = new ArrayList<>();
    }

    public static String luaXToken2Str(LexState lexState, int token){

        if(token < FIRST_RESERVED){
            if(LCtype.isPrint(token)){
                return format("'%c'",token);
            }
            return format("'<\\%d>'",token);
        } else{
            String str = luaX_tokens[token - FIRST_RESERVED];
            if(token < TK_EOS.t){
                return format("'%s'",str);
            }
            return str;
        }

    }

    /**
     *增加行号
     */
    public static void incLineNumber(LexState ls){
        int old = ls.current;
        next(ls);
        if(currIsNewLine(ls) && ls.current != old){
            next(ls);
        }
        ls.linenumber++;
    }

    public static boolean check_next1(LexState ls,int c){
        if(ls.current == c){
            next(ls);
            return true;
        }
        return false;
    }

    public static boolean check_next2(LexState ls,String str){
        if(ls.current == str.charAt(0) || ls.current == str.charAt(1)){
            saveAndNext(ls);
            return true;
        }
        return false;
    }



    public static String format(String fom,Object args){
        return new Formatter().format(fom,args).toString();
    }
}
