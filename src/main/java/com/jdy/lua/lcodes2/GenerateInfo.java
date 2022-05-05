package com.jdy.lua.lcodes2;

import com.jdy.lua.lparser.ExpKind;
import lombok.Data;

import static com.jdy.lua.lcodes.LCodes.NO_JUMP;
import static com.jdy.lua.lcodes.LCodes.addK;

@Data
public class GenerateInfo {

    private ExpKind kind;


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

    public static GenerateInfo intInfo(long i){
        GenerateInfo generateInfo = info(ExpKind.VKINT);
        generateInfo.setIVal(i);
        return generateInfo;
    }
    public static GenerateInfo floatInfo(double f){
        GenerateInfo generateInfo = info(ExpKind.VKFLT);
        generateInfo.setFloatVal(f);
        return generateInfo;
    }

    public static GenerateInfo strInfo(String str){
        GenerateInfo generateInfo = info(ExpKind.VKSTR);
        generateInfo.setStr(str);
        return generateInfo;
    }

    public static GenerateInfo info(ExpKind kind){
        GenerateInfo generateInfo = new GenerateInfo(kind);
        generateInfo.setFJmp(NO_JUMP);
        generateInfo.setTJmp(NO_JUMP);
        return generateInfo;
    }

}
