package com.jdy.lua.lcodes;

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
}
