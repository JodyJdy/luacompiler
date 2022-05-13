package com.jdy.lua.lcodes2;


import com.jdy.lua.lparser2.VirtualLabel;
import lombok.Data;

@Data
public class ExprDesc {
    int reg;
    int n;
    int k;
    int kind;
    int info;
    boolean isAnd;
    VirtualLabel trueLabel;
    VirtualLabel falseLabel;
    VirtualLabel endLabel;
    /**
     * 是否是jump指令
     */
    boolean isJump;
}
