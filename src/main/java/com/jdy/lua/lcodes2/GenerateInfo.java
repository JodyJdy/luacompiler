package com.jdy.lua.lcodes2;

import lombok.Data;

@Data
public class GenerateInfo {
    private int dataType;
    /**
     * 如果是常量，常量里的下标
     */
    private int kIndex;
    /**
     * 如果放在寄存器里面，寄存器下标
     */
    private int regIndex;
    /**
     * 如果是UpVal，Upval下标
     */
    private int upValIndex;


    /**
     * 整数
     */
    private long iVal;
    /**
     * 浮点数
     */
    private double floatVal;
    /**
     * 字符串
     */
    private String str;

    private int info;

    int indexForTable;
    int table;  /** table (register or upvalue) */
    /** for local variables */
    int registerIndex;  /** register holding the variable */
    int actVarIndex;  /** compiler indexForTable (in 'actvar.arr')  */

    /** 真假出口*/
    int tJmp;
    int fJmp;






    public GenerateInfo(int dataType){
        this.dataType = dataType;
    }

    public GenerateInfo(){

    }

}
