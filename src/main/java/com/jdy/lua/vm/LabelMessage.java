package com.jdy.lua.vm;

import lombok.Getter;

@Getter
public class LabelMessage {

    /**
     * 程序指针
     */
    private final int pc;
    /**
     * 使用的寄存器数目
     */
    private final int usedReg;

    public LabelMessage(int pc, int usedReg) {
        this.pc = pc;
        this.usedReg = usedReg;
    }
}
