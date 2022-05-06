package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class LocalVar {
    String name;
    int startpc;
    int endpc;
    int scopeLevel;
    /**
     * 寄存器中的位置
     */
    int slot;
}
