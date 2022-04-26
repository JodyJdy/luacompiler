package com.jdy.lua.lparser;

public class ExpDesc {
    ExpKind k;
    long ival;    /** for VKINT */
    double nval;  /** for VKFLT */
    String strval;  /** for VKSTR */
    int info;  /** for generic use */
    /** for indexed variables */
    short idx;  /** index (R or "long" K) */
    int tt;  /** table (register or upvalue) */
    /** for local variables */
    int ridx;  /** register holding the variable */
    short vidx;  /** compiler index (in 'actvar.arr')  */
    
    /** 真假出口*/
    int t;
    int f;
}
