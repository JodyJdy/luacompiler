package com.jdy.lua.vm;

/**
 * @author jdy
 * @title: Upval
 * @description:
 * @data 2023/9/18 16:35
 */
public class UpVal {
    /**
     * 上级的变量
     */
    private StackElement up;

    public StackElement getUp() {
        return up;
    }

    public int getIndex() {
        return index;
    }

    public UpVal(StackElement up, int index) {
        this.up = up;
        this.index = index;
    }
    /**
     * 下标
     */
    private int index;
}
