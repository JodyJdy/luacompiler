package com.jdy.lua.lparser2;


public class LocVarInfo {
    LocVarInfo prev;
    String name;
    int scopeLv;
    int slot;
    int startPC;
    int endPC;
    /**
     * 标识一个变量有没有被 当初 UpVal使用
     */
    boolean captured;
}
