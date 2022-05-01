package com.jdy.lua.lparser;

/**
 * 用链表 存储左侧的 赋值变量， lua可以对多个变量赋值
 */
public class LeftAssignVars {
    LeftAssignVars prev;
    /**
     * 变量 (local,global,upvalue,or indexed)
     */
    ExpDesc v = new ExpDesc();
}

