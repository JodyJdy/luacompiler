package com.jdy.lua.lcodes;

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
}
