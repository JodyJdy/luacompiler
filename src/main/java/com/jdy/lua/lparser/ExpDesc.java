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
    int indexForTable;  /** indexForTable (R or "long" K) */
    int table;  /** table (register or upvalue) */
    /** for local variables */
    int registerIndex;  /** register holding the variable */
    int actVarIndex;  /** compiler indexForTable (in 'actvar.arr')  */
    
    /** 真假出口*/
    int tJmp;
    int fJmp;

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
        this.indexForTable = e2.indexForTable;
        this.table = e2.table;
        this.registerIndex = e2.indexForTable;
        this.actVarIndex =e2.actVarIndex;
        this.tJmp = e2.tJmp;
        this.fJmp =e2.fJmp;
    }


}
