package com.jdy.lua.vm;

/**
 *
 * pc值 动态调整的 标签
 * @author jdy
 * @title: VirtualLabel
 * @data 2023/9/18 16:23
 */
public class DynamicLabel {
    /**
     * 当前 label的位置
     */
    private Integer  pc;

    public DynamicLabel() {
    }
    public DynamicLabel(int pc){
        this. pc = pc;
    }

    public Integer getPc() {
        return pc;
    }

    public void setPc(Integer pc) {
        this.pc = pc;
    }
}
