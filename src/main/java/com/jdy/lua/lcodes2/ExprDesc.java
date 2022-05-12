package com.jdy.lua.lcodes2;


import com.jdy.lua.lparser2.VirtualLabel;
import lombok.Data;

@Data
public class ExprDesc {
    int reg;
    int n;
    int k;
    int kind;
    boolean isAnd;
    VirtualLabel trueLabel;
    VirtualLabel falseLabel;
    VirtualLabel endLabel;
    /**
     * 跳转指令pc
     */
    int jmp;
}
