package com.jdy.lua.ltm;

/**
 * 一些运算的枚举
 */
public enum TMS {

    TM_INDEX(0),
    TM_NEWINDEX(1),
    TM_GC(2),
    TM_MODE(3),
    TM_LEN(4),
    TM_EQ(5),  /* last tag method with fast access */
    TM_ADD(6),
    TM_SUB(7),
    TM_MUL(8),
    TM_MOD(9),
    TM_POW(10),
    TM_DIV(11),
    TM_IDIV(12),
    TM_BAND(13),
    TM_BOR(14),
    TM_BXOR(15),
    TM_SHL(16),
    TM_SHR(17),
    TM_UNM(18),
    TM_BNOT(19),
    TM_LT(20),
    TM_LE(21),
    TM_CONCAT(22),
    TM_CALL(23),
    TM_CLOSE(24),
    TM_N(25);		/* number of elements in the enum */

    int t;
    TMS(int t){
        this.t = t;
    }

    public int getT(){
        return t;
    }

    public static TMS getTMS(int i){
        for(TMS s : TMS.values()){
            if(s.t == i){
                return s;
            }
        }
        return TM_N;
    }
}
