package com.jdy.lua.vm;

/**
 * @author jdy
 * @title: Upval
 * @description:
 * @data 2023/9/18 16:35
 */
public class UpVal {
    public void setUp(StackElement up) {
        this.up = up;
    }

    /**
     * 上级的变量
     */
    private StackElement up;

    @Override
    public String toString() {
        return "UpVal{" +
                "up=" + up +
                ", index=" + index +
                '}';
    }

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
    private final int index;
}
