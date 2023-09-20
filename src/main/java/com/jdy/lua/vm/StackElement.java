package com.jdy.lua.vm;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.Value;

/**
 * @author jdy
 * @title: StackElement
 * @description:
 * @data 2023/9/18 14:46
 */
public class StackElement {
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * 是否是本地变量
     */
    private boolean isLocalVar = false;

    /**
     * 如果是本地变量 下标
     */
    private int index;
    /**
     * 如果是本地变量 变量名
     */
    private String varName;
    /**
     * 是否闭包引用
     */
    private boolean  capture = false;
    /**
     * 值
     */
    private Value value;

    public StackElement(Value value,int index) {
        this.value = value;
        this.index = index;
    }

    public StackElement(String name, Value val,int index) {
        this.varName = name;
        this.value = val;
        this.index = index;
        this.isLocalVar = true;
    }

    public StackElement(boolean isLocalVar, boolean capture, Value value) {
        this.isLocalVar = isLocalVar;
        this.capture = capture;
        this.value = value;
    }


    public boolean isLocalVar() {
        return isLocalVar;
    }

    public int getIndex() {
        return index;
    }

    public String getVarName() {
        return varName;
    }

    public boolean isCapture() {
        return capture;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StackElement{" +
                "isLocalVar=" + isLocalVar +
                ", index=" + index +
                ", varName='" + varName + '\'' +
                ", capture=" + capture +
                ", value=" + value +
                '}';
    }


    public boolean isFunc(){
        return value.type() == DataTypeEnum.FUNCTION && value instanceof FuncInfo;
    }
    public int funcIndex(){
        return  ((FuncInfo)value).getGlobalFuncIndex();
    }
}
