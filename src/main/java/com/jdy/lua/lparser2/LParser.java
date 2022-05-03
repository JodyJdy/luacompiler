package com.jdy.lua.lparser2;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import com.jdy.lua.lex.Lex;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lex.TokenEnum;
import com.jdy.lua.lparser.FuncState;
import com.jdy.lua.lparser2.expr.*;
import com.jdy.lua.lparser2.statement.*;

import static com.jdy.lua.lcodes.BinOpr.*;
import static com.jdy.lua.lcodes.LCodes.luaK_codeABC;
import static com.jdy.lua.lex.Lex.*;
import static com.jdy.lua.lex.Lex.luaX_Next;
import static com.jdy.lua.lex.TokenEnum.*;
import static com.jdy.lua.lopcodes.OpCode.OP_VARARG;
import static com.jdy.lua.lparser.ExpKind.*;
import static com.jdy.lua.lparser.ExpKind.VVARARG;
import static com.jdy.lua.lparser.LParser.UNARY_PRIORITY;

public class LParser {
    public static BlockStatement block(LexState ls){
        return null;
    }
    public static Statement statement(LexState ls){
        int line = ls.getLinenumber();
        switch (ls.getCurTokenEnum()){

            case IF: {  /* stat -> ifstat */
                return ifStat(ls, line);

            }
            case WHILE: {  /* stat -> whilestat */
                return whileStat(ls, line);

            }
            case DO: {  /* stat -> DO block END */
                luaX_Next(ls);  /* skip DO */
                Statement s = block(ls,line);
                checkMatch(ls, END, DO, line);
                return s;

            }
            case FOR: {  /* stat -> forstat */
                return forStat(ls, line);

            }
            case REPEAT: {  /* stat -> repeatstat */
                return repeatStat(ls, line);

            }
            case FUNCTION: {  /* stat -> funcstat */
                return funcStat(ls, line);

            }
            case LOCAL: {  /* stat -> localstat */
                luaX_Next(ls);  /* skip LOCAL */
                if (testNext(ls, FUNCTION))  /* local function? */
                    return localFunc(ls,line);
                else
                    return localStat(ls,line);

            }
            case DOU_COLON: {  /* stat -> label */
                luaX_Next(ls);  /* skip double colon */
                return labelStat(ls, line);

            }
            case RETURN: {  /* stat -> retstat */
                luaX_Next(ls);  /* skip RETURN */
                return retStat(ls,line);

            }
            case BREAK: {  /* stat -> breakstat */
                return breakStat(ls,line);

            }
            case GOTO: {  /* stat -> 'goto' NAME */
                luaX_Next(ls);  /* skip 'goto' */
                return gotoStat(ls,line);

            }
            case SEMICON:{
                luaX_Next(ls);
                return null;
            }
            default:
                return exprStat(ls,line);
        }
    }

    public static IfStatement ifStat(LexState ls,int line){
        IfStatement ifStatement;

        Expr cond = expr(ls);
        BlockStatement block = block(ls);
        ifStatement = new IfStatement(cond,block);
        while(ls.getCurTokenEnum() == ELSEIF){
            ifStatement.addElseThenCond(expr(ls));
            ifStatement.addElseThenBlock(block(ls));
        }
        if(testNext(ls,ELSE)){
            ifStatement.setElseBlock(block(ls));
        }
        checkMatch(ls,END,IF,line);
        return ifStatement;
    }
    public static WhileStatement whileStat(LexState ls, int line){
        Expr cond = expr(ls);
        checkNext(ls,DO);
        BlockStatement block = block(ls);
        checkMatch(ls,END,WHILE,line);
        return new WhileStatement(cond,block);
    }
    public static BlockStatement block(LexState ls, int line){
        return null;
    }
    public static ForStatement forStat(LexState ls, int line){

        return null;
    }
    public static RepeatStatement repeatStat(LexState ls,int line){
        luaX_Next(ls);
        BlockStatement block = block(ls);
        checkMatch(ls,UNTIL,REPEAT,line);
        Expr cond = expr(ls);

        return new RepeatStatement(block,cond);
    }
    public static FunctionStat funcStat(LexState ls, int line){
        return null;
    }
    public static LocalStatement localStat(LexState ls,int line){
        return null;
    }
    public static FunctionStat localFunc(LexState ls,int line){
        return null;
    }
    public static LabelStatement labelStat(LexState ls,int line){
        checkNext(ls,DOU_COLON);
        LabelStatement labelStatement = new LabelStatement(new NameExpr(ls.getCurrTk().getS()));
        checkNext(ls,DOU_COLON);
        return labelStatement;
    }
    public static ReturnStatement retStat(LexState ls,int line){
        return null;
    }
    public static BreakStatement breakStat(LexState ls,int line){
        return null;
    }
    public static GotoStatement gotoStat(LexState ls,int line){
        return null;
    }
    public static ExprStatement exprStat(LexState ls,int line){
        return null;
    }
    public static ExprList exprList(LexState ls){
        ExprList exprList = new ExprList();
        exprList.addExpr(expr(ls));
        while(testNext(ls,COMMA)){
            exprList.addExpr(expr(ls));
        }
        return exprList;
    }
    public static Expr expr(LexState ls){
        return subExpr(ls,0);
    }

    public static UnOpr getUnopr(TokenEnum op){
        switch (op){
            case SUB:return UnOpr.OPR_MINUS;
            case BITXOR: return UnOpr.OPR_BNOT;
            case LEN: return UnOpr.OPR_LEN;
            case NOT:return UnOpr.OPR_NOT;
            default:
                return UnOpr.OPR_NOUNOPR;
        }
    }
    public static int[][] priority ={
            {10, 10}, {10, 10},           /* '+' '-' */
            {11, 11}, {11, 11},           /* '*' '%' */
            {14, 13},                  /* '^' (right associative) */
            {11, 11}, {11, 11},           /* '/' '//' */
            {6, 6}, {4, 4}, {5, 5},   /* '&' '|' '~' */
            {7, 7}, {7, 7},           /* '<<' '>>' */
            {9, 8},                   /* '..' (right associative) */
            {3, 3}, {3, 3}, {3, 3},   /* ==, <, <= */
            {3, 3}, {3, 3}, {3, 3},   /* ~=, >, >= */
            {2, 2}, {1, 1}            /* and, or */
    };
    /**
     * 单运算符的优先级
     */
    public static int UNARY_PRIORITY = 12;

    public static BinOpr getBinopr(TokenEnum op){
        switch (op){
            case ADD:return BinOpr.OPR_ADD;
            case SUB: return OPR_SUB;
            case MUL: return OPR_MUL;
            case MOD: return OPR_MOD;
            case POW: return OPR_POW;
            case DIV: return OPR_DIV;
            case BITAND: return OPR_BAND;
            case BITOR: return OPR_BOR;
            case BITXOR: return OPR_BXOR;
            case LT: return OPR_LT;
            case GT: return OPR_GT;
            case IDIV: return OPR_IDIV;
            case LSHIFT: return OPR_SHL;
            case RSHIFT: return OPR_SHR;
            case CAT: return OPR_CONCAT;
            case NE: return OPR_NE;
            case EQ: return OPR_EQ;
            case LE: return OPR_LE;
            case GE: return OPR_GE;
            case AND: return OPR_AND;
            case OR: return OPR_OR;
            default:
                return OPR_NOBINOPR;
        }
    }
    public static SubExpr subExpr(LexState ls,int limit){
        BinOpr op;
        UnOpr uop;
        SubExpr subExpr;
        uop = getUnopr(ls.getCurTokenEnum());
        if(uop != UnOpr.OPR_NOUNOPR){
            luaX_Next(ls);
            SubExpr exp = subExpr(ls,UNARY_PRIORITY);
            subExpr = new SubExpr(uop,exp);
        } else{
            SimpleExpr expr = simpleExp(ls);
            subExpr = new SubExpr(expr);
        }
        op = getBinopr(ls.getCurTokenEnum());
        while(op != OPR_NOBINOPR && priority[op.getOp()][0] > limit){
            SubExpr subExpr2 = subExpr(ls,priority[op.getOp()][1]);
            subExpr.setBinOpr(op);
            subExpr.setSubExpr2(subExpr2);
            //再套一层
            subExpr = new SubExpr(subExpr);
            //获取下一个操作符
            op = subExpr2.getBinOpr();
        }
        return subExpr;
    }
    public static SimpleExpr simpleExp(LexState ls){

        Expr expr;
        switch (ls.getCurTokenEnum()){
            case BIG_LEFT:
                expr = constructor(ls);
                break;
            case FLOAT:
                expr = new FloatExpr(ls.getCurrTk().getR());
                luaX_Next(ls);
                break;
            case INT:
                expr = new IntExpr(ls.getCurrTk().getI());
                luaX_Next(ls);
                break;
            case STRING: {
                expr = new StringExpr(ls.getCurrTk().getS());
                luaX_Next(ls);
                break;
            }
            case NIL: {
                expr = new NilExpr();
                luaX_Next(ls);
                break;
            }
            case TRUE: {
                expr = new TrueExpr();
                luaX_Next(ls);
                break;
            }
            case FALSE: {
                expr = new FalseExpr();
                break;
            }
            case VARARG: {
                expr = new VarargExpr();
                luaX_Next(ls);
                break;
            }

            case FUNCTION: {
                 luaX_Next(ls);
                 BlockStatement st = block(ls, ls.getLinenumber());
                 expr = new FunctionBody(st);
                 break;
            }
            default: {
                expr =suffixedExp(ls);
                break;
            }

        }
        return new SimpleExpr(expr);
    }
    public static PrimaryExpr primaryExpr(LexState ls){
        Expr expr;
        if(ls.getCurTokenEnum()== SMALL_LEFT){
            luaX_Next(ls);
            expr = expr(ls);
            checkNext(ls,SMALL_RIGHT);
            return new PrimaryExpr(expr);
        } else if(ls.getCurTokenEnum() == NAME){
            return new PrimaryExpr(new NameExpr(ls.getCurrTk().getS()));
        }
        return null;
    }

    public static NameExpr fieldSel(LexState ls){
        luaX_Next(ls);
        return new NameExpr(ls.getCurrTk().getS());
    }
    public static SuffixedExp suffixedExp(LexState ls){
        Expr expr = primaryExpr(ls);
        SuffixedExp suffixedExp = new SuffixedExp(expr);
        for(;;){
            switch (ls.getCurTokenEnum()){
                case DOT:
                    NameExpr nameExpr = fieldSel(ls);
                    suffixedExp.setNameExpr(nameExpr);
                    suffixedExp.setHasDot(true);
                    break;
                case MID_LEFT: {
                    TableIndex tableIndex = tableIndex(ls);
                    suffixedExp.setExpr(tableIndex);
                    break;
                }
                case COLON: {

                    luaX_Next(ls);
                    NameExpr nameExpr1 = new NameExpr(ls.getCurrTk().getS());
                    FuncArgs funcArgs = funcArgs(ls);
                    suffixedExp.setNameExpr(nameExpr1);
                    suffixedExp.setFuncArgs(funcArgs);
                    suffixedExp.setHasColon(true);
                    break;
                }
                case SMALL_LEFT: case BIG_LEFT: case STRING:
                    suffixedExp.setFuncArgs(funcArgs(ls));
                    break;

                default:
                    return suffixedExp;
            }
            //嵌套结构，因为有 a.b.c.d这种情况
            suffixedExp = new SuffixedExp(suffixedExp);
        }

    }
    public static TableConstructor constructor(LexState ls){
        TableConstructor cons  = new TableConstructor();
        checkNext(ls,BIG_LEFT);

        do {
            if(ls.getCurTokenEnum() == BIG_RIGHT){
                break;
            }
            Field field = field(ls);
            if(field.getTableField() != null){
                cons.addTableFields(field.getTableField()) ;
            } else{
                cons.addTableListFieds(field.getTableListField());
            }

        }while(testNext(ls,COMMA) || testNext(ls,SEMICON));


        checkNext(ls,BIG_RIGHT);
        return cons;
    }
    public static FuncArgs funcArgs(LexState ls){
        FuncArgs funcArgs;
        switch (ls.getCurTokenEnum()){
            case SMALL_LEFT:
                luaX_Next(ls);
                if(ls.getCurTokenEnum() == SMALL_RIGHT){
                    funcArgs = new FuncArgs();
                } else{
                    funcArgs = new FuncArgs();
                    ExprList exprList = exprList(ls);
                    funcArgs.addExprList(exprList);
                }
                break;
            case BIG_LEFT:
                TableConstructor constructor = constructor(ls);
                funcArgs = new FuncArgs(constructor);
                break;
            case STRING:
                funcArgs = new FuncArgs(new StringExpr(ls.getCurrTk().getS()));
                luaX_Next(ls);
                break;
                default:
                    funcArgs = null;
                    System.err.println("lost func args");

        }
        return funcArgs;
    }
    public static TableListField listField(LexState ls){
        return new TableListField(expr(ls));
    }
    public static TableField recField(LexState ls){
        Expr left,right;
        if(ls.getCurTokenEnum() == NAME){
            left = new NameExpr(ls.getCurrTk().getS());
        } else{
            left = tableIndex(ls);
        }
        checkNext(ls,ASSIGN);
        right = expr(ls);
        return new TableField(left,right);
    }

    public static Field field(LexState ls) {
        Field f;
        switch (ls.getCurTokenEnum()) {
            case NAME:
                if (luaX_lookahead(ls) != ASSIGN) {
                    f = new Field(listField(ls));
                } else {
                    f = new Field(recField(ls));
                }
                break;
            case MID_LEFT:
                f = new Field(recField(ls));
                break;
            default:
                f = new Field(listField(ls));
        }
        return f;
    }
    public static TableIndex tableIndex(LexState ls){
        luaX_Next(ls);
        Expr e = expr(ls);
        luaX_Next(ls);
        return new TableIndex(e);
    }
}
