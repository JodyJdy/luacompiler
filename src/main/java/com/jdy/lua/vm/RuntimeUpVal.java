package com.jdy.lua.vm;

/**
 * @author jdy
 * @title: RuntimeUpVal
 * @description:
 * @data 2023/9/20 13:48
 */
public class RuntimeUpVal {

    /**
     * 运行时的UpVal
     */
    final StackElement up;


    public RuntimeUpVal(StackElement up, int index) {
        this.up = up;
        this.index = index;
    }

    final int index;

}
