package com.jdy.lua.lparser2;

import lombok.Data;

@Data
public class LocVarInfo {
    /**
     * 会有重名，作用域不一样的变量
     *
     * {
     *   local a;
     *     {
     *         local a
     *     }
     * }
     */
    LocVarInfo prev;
    String name;
    int scopeLv;
    int slot;
    int startPC;
    int endPC;
    /**
     * 标识一个变量有没有被 当成 UpVal使用
     */
    boolean captured;
}
