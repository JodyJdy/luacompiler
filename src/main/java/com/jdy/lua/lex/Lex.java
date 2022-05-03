package com.jdy.lua.lex;

import com.jdy.lua.lstate.LuaState;
import com.jdy.lua.lstring.LString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static com.jdy.lua.lctype.LCtype.*;
import static com.jdy.lua.lex.TokenEnum.*;

public class Lex {

    /**
     * 文件结尾
     */
    public static final int EOZ = -1;

    public static String LUA_ENV = "_ENV";



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


    }

    void luaX_SetInput(LuaState l, LexState lexState, String source, int firstChar) {
        lexState.L = l;
        lexState.current = firstChar;
        lexState.fs = null;
        lexState.linenumber = 1;
        lexState.lastline = 1;
        lexState.source = source;
        lexState.envn = LString.newStr(lexState.L, LUA_ENV);
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

    private static boolean currIsNewLine(LexState lexState) {
        return lexState.current == '\n' || lexState.current == '\r';
    }

    private static void saveAndNext(LexState lexState) {
        lexState.buffer.add(lexState.current);
        next(lexState);
    }

    private static void save(LexState lexState, int c) {
        lexState.buffer.add(c);
    }

    private static String buffer2Str(LexState lexState, int start, int len) {

        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + len; i++) {
            int ch = lexState.buffer.get(i);
            sb.append((char) ch);
        }
        return sb.toString();

    }

    private static void bufferRemove(LexState lexState, int n) {
        List<Integer> lex = lexState.buffer;
        int i = 0;
        while (i < n) {
            lex.remove(lex.size() - 1);
            i++;
        }
    }

    private static void resetBuffer(LexState lexState) {
        lexState.buffer = new ArrayList<>();
    }

    public static String luaXToken2Str(LexState lexState, TokenEnum token) {


        return format("'%s'", token.getStr());


    }

    /**
     * 增加行号
     */
    private static void incLineNumber(LexState ls) {
        int old = ls.current;
        next(ls);
        if (currIsNewLine(ls) && ls.current != old) {
            next(ls);
        }
        ls.linenumber++;
    }

    private static boolean check_next1(LexState ls, int c) {
        if (ls.current == c) {
            next(ls);
            return true;
        }
        return false;
    }
    public static boolean checkNext(LexState ls,TokenEnum token){
        if(ls.getCurTokenEnum() == token){
            luaX_Next(ls);
            return true;
        }
        return false;
    }
    private static boolean check_next2(LexState ls, String str) {
        if (ls.current == str.charAt(0) || ls.current == str.charAt(1)) {
            saveAndNext(ls);
            return true;
        }
        return false;
    }

    /**
     * 读取数字
     */

    private static TokenEnum read_numeral(LexState ls, Token token) {
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
        if(isHexDigit(first)){
            if(isHex){
                val = Character.digit(first,16);
            } else{
                val = Character.digit(first,10);
            }
        }
        if (isHexDigit(ls.current) || ls.current == '.') {

            //读取整数数字
            if (ls.current != '.') {
                for (; ; ) {
                    if (isHex && isHexDigit(ls.current)) {
                        val = val * 16 + Character.digit(ls.current, 16);
                    } else if (isDigit(ls.current)) {
                        val = val * 10 + Character.digit(ls.current, 10);
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
                next(ls);
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
            return FLOAT;
        } else {
            token.i = val;
            return INT;
        }
    }

    /**
     * 多行注释以 -- 开头,内容以 [=*[ 开头,  ]=*] 结尾, *代表有多个等于号必须是成对的
     * <p>
     * 跳过开头的符号， 或者结尾的符号
     */
    private static int skipSeq(LexState ls) {
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
    private static void readLongString(LexState ls, Token token, int seq) {
        saveAndNext(ls);
        if (currIsNewLine(ls)) {
            incLineNumber(ls);
        }

        endLoop:
        for (; ; ) {
            switch (ls.current) {
                case EOZ:
                    System.err.println("error");
                    break endLoop;
                case ']':
                    if (skipSeq(ls) == seq) {
                        saveAndNext(ls);
                        break endLoop;
                    }
                    break;
                case '\n':
                case '\r': {
                    save(ls, '\n');
                    incLineNumber(ls);
                    if (token == null) {
                        resetBuffer(ls);
                    }
                    break;
                }
                default:
                    if (token != null) {
                        saveAndNext(ls);
                    } else {
                        next(ls);
                    }
            }
        }
        if (token != null) {
            //将 seq 之间的部分生成字符串
            token.s = buffer2Str(ls, seq, ls.buffer.size() - 2 * seq);
        }
    }

    /**
     * del means delimeter
     */
    private static void readString(LexState ls, int del, Token token) {
        //save delimeter
        saveAndNext(ls);
        while (ls.current != del) {
            switch (ls.current){
                case EOZ: case '\n': case '\r':
                    System.err.println("error");
                    break;
                //转义符
                case '\\': {
                    //最后存储的字符 \\a 那么存储 \a
                    int c = 0;
                    //默认执行类型是0
                    int executeType = 0;
                    saveAndNext(ls);
                    switch (ls.current) {
                        case 'b': c = '\b';break;
                        case 'f': c = '\f'; break;
                        case 'n': c = '\n';break;
                        case 'r': c = '\r';break;
                        case 't': c = '\t';break;
                        case '\n':
                        case '\r':
                            incLineNumber(ls);
                            c = '\n';
                            executeType = 1;
                            break;
                        case '\\': case '\"': case '\'':
                            c = ls.current;
                            break;
                        case EOZ: default: {
                            executeType = -1;
                            break;
                        }
                    }
                    if(executeType == 0){
                        next(ls);
                        //remove去除存储的转义符号
                        bufferRemove(ls, 1);
                        save(ls, c);
                    }
                    if(executeType == 1){
                        //remove去除存储的转义符号
                        bufferRemove(ls, 1);
                        save(ls, c);
                    }
                    break;
                }
                default:saveAndNext(ls);
            }
        }
        saveAndNext(ls);
        token.s = buffer2Str(ls,1,ls.buffer.size() - 2);
    }

    public static void llex(LexState ls,boolean cur){
        Token t = cur ? ls.getCurrTk() : ls.getLookahead();
        t.setToken(llex(ls,t));
        System.out.println(t);
    }

    private static TokenEnum llex(LexState ls,Token token){
        resetBuffer(ls);
        for(;;){
            switch (ls.current){
                //换行
                case '\n': case '\r':{
                    incLineNumber(ls);
                    break;
                }
                case ' ': case '\f': case '\t': {  /* spaces */
                    next(ls);
                    break;
                }
                //可能是操作符，也可能是注释
                case '-':{
                    next(ls);
                    if (ls.current != '-') return SUB;
                    //注释
                    next(ls);
                    //长注释
                    if(ls.current == '['){
                        int seq = skipSeq(ls);
                        resetBuffer(ls);
                        if(seq >=2){
                            readLongString(ls,null,seq);
                            resetBuffer(ls);
                            break;
                        }
                    }
                    //短注释
                    while(!currIsNewLine(ls) && ls.current != EOZ){
                        next(ls);
                    }
                    break;
                }
                case '[':{
                  // long string 或者 符号[,例如 a[x]
                    int seq = skipSeq(ls);
                    if(seq >=2){
                        readLongString(ls,token,seq);
                        return STRING;
                    } else if(seq == 0){
                        System.err.println("invalid long string delimiter");
                    }
                    // 普通 [ 符号
                    return MID_LEFT;
                }

                case '=': {
                    next(ls);
                    if (check_next1(ls, '=')) return EQ;  /* '==' */
                    else return ASSIGN;
                }
                case '<': {
                    next(ls);
                    if (check_next1(ls, '=')) return LE;  /* '<=' */
                    else if (check_next1(ls, '<')) return LSHIFT;  /* '<<' */
                    else return LT;
                }
                case '>': {
                    next(ls);
                    if (check_next1(ls, '=')) return GE;  /* '>=' */
                    else if (check_next1(ls, '>')) return RSHIFT;  /* '>>' */
                    else return GT;
                }
                case '/': {
                    next(ls);
                    if (check_next1(ls, '/')) return IDIV;  /* '//' */
                    else return DIV;
                }
                case '~': {
                    next(ls);
                    if (check_next1(ls, '=')) return NE;  /* '~=' */
                    else return BITXOR;
                }
                case ':': {
                    next(ls);
                    if (check_next1(ls, ':')) return DOU_COLON;  /* '::' */
                    else return COLON;
                }
                case '"': case '\'': {  /* short literal strings */
                    readString(ls, ls.current, token);
                    return STRING;
                }
                case '.': {  /* '.', '..', '...', or number */
                    saveAndNext(ls);
                    if (check_next1(ls, '.')) {
                        if (check_next1(ls, '.'))
                            return VARARG;  /* '...' */
                        else return CAT;   /* '..' */
                    }
                    else if (!isDigit(ls.current)) return DOT;
                    else return read_numeral(ls, token);
                }
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9': {
                    return read_numeral(ls, token);
                }
                case EOZ: {
                    return EOF;
                }
                /** identifier or reserved word? */
                default:{
                    if(isAlpha(ls.current)){
                        do{
                            saveAndNext(ls);
                        }while (isalNum(ls.current));
                        token.s = buffer2Str(ls,0,ls.buffer.size());
                        //判断是否是保留词汇
                         TokenEnum r = TokenEnum.reservedToken(token.s);
                        if(r != null){
                           return r;
                        }
                        return NAME;
                    }
                    int c = ls.current;
                    next(ls);
                    return TokenEnum.getTokenEnum((char)c);
                }
            }
        }

    }

    public static void luaX_Next(LexState l){
        l.setLastline(l.getLinenumber());
        if(l.getNextTokenEnum()!= EOF){
            l.setCurrTk(l.getLookahead());
            //重置lookhead
            l.resetLookAhead();
        } else{
           llex(l,true);
        }
    }

    public static TokenEnum luaX_lookahead (LexState ls) {
        llex(ls,false);
        return ls.getNextTokenEnum();
    }

    /**
     * 检查token 是否是c，如果是 读取下一个token

     */
    public static boolean testNext (LexState ls, TokenEnum c) {
        if (ls.getCurTokenEnum() == c) {
            luaX_Next(ls);
            return true;
        }
        return false;
    }

    /**
     * 检查 token 是不是 c，如果不是报错

     */
    public static void check (LexState ls,TokenEnum c) {
        if (ls.getCurTokenEnum() != c)
            System.err.println("expecd:"+luaXToken2Str(ls,c));;
    }

    /**
     * 期望是 waht
     * line 是 where

     */
    public static void checkMatch(LexState ls, TokenEnum what, TokenEnum who, int where){
        if(!testNext(ls,what)){
            System.out.println("错误");
        }
    }

    private static String format(String fom, Object args) {
        return new Formatter().format(fom, args).toString();
    }
}
