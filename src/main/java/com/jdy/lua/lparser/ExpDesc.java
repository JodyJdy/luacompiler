package com.jdy.lua.lparser;

import lombok.Data;

@Data
public class ExpDesc implements Cloneable {
    ExpKind k;
    long ival;    /** for VKINT */
    double nval;  /** for VKFLT */
    String strval;  /** for VKSTR */
    int info;  /** for generic use */
    /** for indexed variables */
    int idx;  /** index (R or "long" K) */
    int tt;  /** table (register or upvalue) */
    /** for local variables */
    int ridx;  /** register holding the variable */
    short vidx;  /** compiler index (in 'actvar.arr')  */
    
    /** 真假出口*/
    int t;
    int f;

    @Override
    public Object clone(){
        ExpDesc obj = null;
        try {
            obj = (ExpDesc) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        obj.setK(this.k);
        return obj;
    }

    public void setFromExp(ExpDesc e2){
        this.k=e2.k;
        this.ival = e2.ival;
        this.setIval(e2.ival);
        this.nval = e2.nval;
        this.strval = e2.strval;
        this.info = e2.info;
        this.idx = e2.idx;
        this.tt = e2.tt;
        this.ridx = e2.idx;
        this.vidx=e2.vidx;
        this.t= e2.t;
        this.f=e2.f;
    }


}
