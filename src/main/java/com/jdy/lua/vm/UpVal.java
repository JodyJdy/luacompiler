package com.jdy.lua.vm;

/**
 * @author jdy
 * @title: Upval
 * @description:
 * @data 2023/9/18 16:35
 */
public class UpVal {
    public void setUp(StackElement up) {
        this.up = up;
    }

    /**
     * 上级的变量
     */
    private StackElement up;

    /**
     * 如果是 父级 就是 1
     * 父级的父级 就是 2
     * 表示向上查找的层级
     */
    private final int level;

    @Override
    public String toString() {
        return "UpVal{" +
                "up=" + up +
                ", index=" + index +
                ",level+" +level +
                '}';
    }

    public StackElement getUp() {
        return up;
    }

    public int getIndex() {
        return index;
    }


    public UpVal(StackElement up, int index,int level) {
        this.up = up;
        this.index = index;
        this.level = level;
    }
    /**
     * 下标
     */
    private final int index;

    public int getLevel() {
        return level;
    }

   public boolean  isFunc(){
       return up.isFunc();
   }
   public int funcIndex(){
        return up.funcIndex();
   }
}
