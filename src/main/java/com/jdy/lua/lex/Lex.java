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

import static com.jdy.lua.lctype.LCtype.*;

import static com.jdy.lua.lex.Reserved.*;

public class Lex {

    /**
     * 文件结尾
     */
    public static int EOZ = -1;

    public static String LUA_ENV = "_ENV";

    /**
     * while 以及之前的字符串 保留到 字符串常量表中
     */
    public static int NUM_RESERVED = Reserved.values().length;

    /**
     * 保留词
     */
    static String luaX_tokens[] = {
            "and", "break", "do", "else", "elseif",
            "end", "false", "for", "function", "goto", "if",
            "in", "local", "nil", "not", "or", "repeat",
            "return", "then", "true", "until", "while",
            "//", "..", "...", "==", ">=", "<=", "~=",
            "<<", ">>", "::", "<eof>",
            "<number>", "<integer>", "<name>", "<string>"
    };

    void luaX_Init(LuaState luaState) {
        TString e = LString.newStr(luaState, LUA_ENV);
        //gc部分代码，先不处理
        //处理保留的关键字
        for (int i = 0; i < NUM_RESERVED; i++) {
            TString s = LString.newStr(luaState, luaX_tokens[i]);
            s.setExtra(i + 1);
        }

    }

    void luaX_SetInput(LuaState l, LexState lexState, String source, int firstChar) {
        lexState.t.token = 0;
        lexState.L = l;
        lexState.current = firstChar;
        lexState.lookahead.token = TK_EOS.t;
        lexState.fs = null;
        lexState.linenumber = 1;
        lexState.lastline = 1;
        lexState.source = source;
        lexState.envn = LString.newStr(lexState.L, LUA_ENV).getContents();

    }


    /**
     * 读取下一个字符
     */
    public static void next(LexState lexState) {
        try {
            lexState.current = lexState.reader.read();
        } catch (IOException e) {
            lexState.current = EOZ;
        }
    }

    public static boolean currIsNewLine(LexState lexState) {
        return lexState.current == '\n' || lexState.current == '\r';
    }

    public static void saveAndNext(LexState lexState) {
        lexState.buffer.add(lexState.current);
        next(lexState);
    }
    public static void save(LexState lexState, int c){
        lexState.buffer.add(c);
    }
    public static String buffer2Str(LexState lexState, int start,int len){

        StringBuilder sb  = new StringBuilder();
        for(int i=start;i<start + len;i++){
            int ch = lexState.buffer.get(start);
            sb.append((char)ch);
        }
        return sb.toString();

    }
    public static void bufferRemove(LexState lexState, int n) {
        List<Integer> lex = lexState.buffer;
        int i = 0;
        while (i < n) {
            lex.remove(lex.size() - 1);
            i++;
        }
    }

    public static void resetBuffer(LexState lexState) {
        lexState.buffer = new ArrayList<>();
    }

    public static String luaXToken2Str(LexState lexState, int token) {

        if (token < FIRST_RESERVED) {
            if (LCtype.isPrint(token)) {
                return format("'%c'", token);
            }
            return format("'<\\%d>'", token);
        } else {
            String str = luaX_tokens[token - FIRST_RESERVED];
            if (token < TK_EOS.t) {
                return format("'%s'", str);
            }
            return str;
        }

    }

    /**
     * 增加行号
     */
    public static void incLineNumber(LexState ls) {
        int old = ls.current;
        next(ls);
        if (currIsNewLine(ls) && ls.current != old) {
            next(ls);
        }
        ls.linenumber++;
    }

    public static boolean check_next1(LexState ls, int c) {
        if (ls.current == c) {
            next(ls);
            return true;
        }
        return false;
    }

    public static boolean check_next2(LexState ls, String str) {
        if (ls.current == str.charAt(0) || ls.current == str.charAt(1)) {
            saveAndNext(ls);
            return true;
        }
        return false;
    }

    /**
     * 读取数字
     */

    public static int read_numeral(LexState ls, Token token) {
        char first = (char) ls.current;
        saveAndNext(ls);
        boolean isHex = false;
        boolean isFloat = false;

        long val = 0;
        double valf = 0.0;

        //16进制
        if (first == '0' && check_next2(ls, "xX")) {
            isHex = true;
        }
        if (isHexDigit(ls.current) || ls.current == '.') {
            //读取整数数字
            if (ls.current != '.') {
                for (; ; ) {
                    if (isHex && isHexDigit(ls.current)) {
                        val = val * 16 + Character.digit(ls.current, 16);
                    } else if (isDigit(ls.current)) {
                        val = val * 16 + Character.digit(ls.current, 10);
                    } else {
                        break;
                    }
                    next(ls);
                }
            }
            //读取小数部分
            double POW = isHex ? 16.0 : 10.0;
            double d = POW;
            if (ls.current == '.') {
                isFloat = true;
                for (; ; ) {
                    if (isHex && isHexDigit(ls.current)) {
                        valf += Character.digit(ls.current, 16) / d;
                    } else if (isDigit(ls.current)) {
                        valf += Character.digit(ls.current, 10) / d;
                    } else {
                        break;
                    }
                    d = d * POW;
                    next(ls);
                }
            }
        }
        if (isFloat) {
            token.r = val + valf;
            return TK_FLT.t;
        } else {
            token.i = val;
            return TK_INT.t;
        }
    }

    /**
     * 多行注释以 -- 开头,内容以 [=*[ 开头,  ]=*] 结尾, *代表有多个等于号必须是成对的
     *
     * 跳过开头的符号， 或者结尾的符号
     */
    public static int skipSeq(LexState ls){
        int count = 0;
        int s = ls.current;
        saveAndNext(ls);
        while (ls.current == '=') {
            saveAndNext(ls);
            count++;
        }
        return ls.current == s ? count + 2 : (count == 0) ? 1 : 0;
    }

    /**
     * seq代表序列开头的部分，开头和结尾要保持一致
     */
    public void readLongString(LexState ls,Token token, int seq){
        int line = ls.linenumber;
        saveAndNext(ls);
        if(currIsNewLine(ls)){
            incLineNumber(ls);
        }
        boolean loop = true;

        endLoop:
        for (; ; ) {
            switch (ls.current){
                case -1 :
                    System.err.println("error");
                    break endLoop;
                case ']':
                    if(skipSeq(ls) == seq){
                        saveAndNext(ls);
                        break endLoop;
                    }
                    break ;
                case '\n': case '\r':{
                        save(ls,'\n');
                        incLineNumber(ls);
                        if(token == null){
                            resetBuffer(ls);
                        }
                        break;
                }
                default:
                    if(token != null){
                        saveAndNext(ls);
                    } else{
                        next(ls);
                    }
            }
        }
        if(token != null){
            //将 seq 之间的部分生成字符串
            token.s = buffer2Str(ls,seq,ls.buffer.size() - 2 * seq);
        }
    }

    /**
     * del means delimeter

     */
    public static void readString(LexState ls,int del,Token token){
        //save delimeter
        saveAndNext(ls);
        while (ls.current != del){

        }

    }



    public static String format(String fom, Object args) {
        return new Formatter().format(fom, args).toString();
    }
}
