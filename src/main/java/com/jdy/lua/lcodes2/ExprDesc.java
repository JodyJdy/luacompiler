package com.jdy.lua.lcodes2;


import com.jdy.lua.lparser2.VirtualLabel;
import lombok.Data;

@Data
public class ExprDesc {
    int reg;
    int n;
    int info;
    boolean isAnd;
    VirtualLabel trueLabel;
    VirtualLabel falseLabel;

    /**
     * tableAccess时存放table，key所在的寄存器
     */
    int tableReg;
    int tableKey;

    /**
     * 是否是jump指令
     */
    boolean isJump;

    /**
     * 不能使用交换引用的方式，因为在Statement中，label是手动创建的
     * Label true ; Label false;
     * 即使 ExprDesc 里面进行了交换，也不影响外部对其的的真假label的认定，因此要交换内容！！！
     */
    public void exchangeLabel(){
        VirtualLabel temp1 = new VirtualLabel();
        temp1.addInstructionList(trueLabel);
        VirtualLabel temp2 = new VirtualLabel();
        temp2.addInstructionList(falseLabel);
        trueLabel.clear();
        falseLabel.clear();
        trueLabel.addInstructionList(temp2);
        falseLabel.addInstructionList(temp1);
    }


}
