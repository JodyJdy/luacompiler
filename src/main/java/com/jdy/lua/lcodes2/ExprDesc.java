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
    /**
     * 是否是jump指令
     */
    boolean isJump;




    public static ExprDesc createExprDesc(int reg,int n){
        ExprDesc desc = new ExprDesc();
        desc.setReg(reg);
        desc.setN(n);
        return desc;
    }
    public static ExprDesc createExprDesc(VirtualLabel trueLabel,VirtualLabel falseLabel){
        ExprDesc desc = new ExprDesc();
        desc.setTrueLabel(trueLabel);
        desc.setFalseLabel(falseLabel);
        return desc;
    }
}
