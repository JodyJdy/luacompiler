package com.jdy.lua.vm;

import com.jdy.lua.data.Value;

/**
 * @author jdy
 * @title: GlobalVal
 * @description:
 * @data 2023/9/18 16:40
 */
public class GlobalVal {
    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Value getVal() {
        return val;
    }

    int index;
    private String name;

    private Value val;

    public GlobalVal(int index, String name, Value val) {
        this.index = index;
        this.name = name;
        this.val = val;
    }
}
