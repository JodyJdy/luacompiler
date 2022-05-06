package com.jdy.lua.lcodes2;

import com.jdy.lua.lparser.ExpKind;
import lombok.Data;

import static com.jdy.lua.lcodes.LCodes.NO_JUMP;
import static com.jdy.lua.lcodes.LCodes.addK;

@Data
public class GenerateInfo {

    private ExpKind kind;



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

    /**
     * 作一般情况下的使用
     */
    private int info;

    /**
     * 表的索引
     */
    int indexForTable;
    /**
     * 表的位置
     */
    int table;
    /**
     * 变量的寄存器位置
     */
    int registerIndex;
    /**dynData中的索引 */
    int actVarIndex;

    /** 真假出口*/
    int tJmp;
    int fJmp;


    public GenerateInfo(){

    }

    public GenerateInfo(ExpKind kind){
        this.kind = kind;
    }

}
