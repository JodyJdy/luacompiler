package com.jdy.lua.lcodes2;


import com.jdy.lua.lparser2.VirtualLabel;

public class ExprDesc {
    int reg;
    int n;
    int k;
    int kind;
    VirtualLabel trueLabel = new VirtualLabel();
    VirtualLabel falseLabel = new VirtualLabel();
    VirtualLabel endLabel = new VirtualLabel();
}
