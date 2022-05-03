package com.jdy.lua.lcodes;

import com.jdy.lua.lex.TokenEnum;

public enum BinOpr {

    /* arithmetic operators */
    OPR_ADD(0), OPR_SUB(1), OPR_MUL(2), OPR_MOD(3), OPR_POW(4),
    OPR_DIV(5), OPR_IDIV(6),
    /* bitwise operators */
    OPR_BAND(7), OPR_BOR(8), OPR_BXOR(9),
    OPR_SHL(10), OPR_SHR(11),
    /* string operator */
    OPR_CONCAT(12),
    /* comparison operators */
    OPR_EQ(13), OPR_LT(14), OPR_LE(15),
    OPR_NE(16), OPR_GT(17), OPR_GE(18),
    /* logical operators */
    OPR_AND(19), OPR_OR(20),
    OPR_NOBINOPR(21);

    int op;
    BinOpr(int op){
        this.op =op;
    }

    public static BinOpr getBinOpr(int op){
        for(BinOpr opr : BinOpr.values()){
            if(opr.op == op){
                return opr;
            }
        }
        return null;
    }
    public int getOp(){
        return op;
    }

    /**
     * 是否是算数运算符， 算数运算符号，可以 常量折叠
     */
    public boolean foldBinaryOp(){
        return this.getOp() <= OPR_SHR.getOp();
    }

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
}
