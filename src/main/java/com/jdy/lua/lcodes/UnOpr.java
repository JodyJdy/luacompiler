package com.jdy.lua.lcodes;

import com.jdy.lua.lex.TokenEnum;

/**
 * 单操作符
 */
public enum UnOpr {

    OPR_MINUS(0), OPR_BNOT(1), OPR_NOT(2), OPR_LEN(3), OPR_NOUNOPR(4);
    int op;
    UnOpr(int op){
        this.op = op;
    }

    public static UnOpr getUnOpr(int op){
        for(UnOpr opr : UnOpr.values()){
            if(opr.op == op){
                return opr;
            }
        }
        return null;
    }

    public int getOp(){
        return op;
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
}
