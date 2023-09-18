package com.jdy.lua.vm;

import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jdy
 * @title: Block
 * @description:
 * @data 2023/9/18 14:16
 */
public class Block {

    /**
     * 引用的 父级 block 中的变量
     */
    private final List<StackElement> upVal = new ArrayList<>();
    private final Map<String, StackElement> upValMap = new HashMap<>();
    /**
     * block范围内的变量
     */
    private final Map<String, StackElement> localVarMap = new HashMap<>();
    /**
     * 全局范围变量
     * 不存在栈里面
     */
    private static final List<Variable> globalVar = new ArrayList<>();
    private static final Map<String, Variable> globalVarMap = new HashMap<>();

    /**
     * 全局 常量
     */
    private static final List<Value> constant = new ArrayList<>();

    /**
     * 当前block的寄存器
     */
    private final List<Value> registers = new ArrayList<>();

    private int used = -1;
    private Block parent;


    /**
     * 申请一个寄存器
     */
    public int allocRegister() {
        used++;
        //用nil填充寄存器的值
        if (used == registers.size()) {
            registers.add(NilValue.NIL);
        }
        return used;
    }

    public void freeRegister() {
        if (used - 1 < 0) {
            throw new RuntimeException("没有足够的寄存器释放");
        }
        used--;
    }

    /**
     * 申请n个寄存器
     * 返回第一个申请的寄存器下标
     */
    public int allocRegister(int n) {
        int size = registers.size();
        for (int i = used + 1; i <= used + n; i++) {
            if (i >= size) {
                registers.add(NilValue.NIL);
            }
        }
        int s = used + 1;
        used += n;
        return s;
    }

    /**
     * 释放n个寄存器
     */
    public void freeRegister(int n) {
        used -= n;
    }

    /**
     * 搜索变量
     */
    public StackElement searchVar(String name) {
        return localVarMap.get(name);
    }

    public StackElement searchParentVar(String name) {
        if (parent != null) {
            StackElement result;
            if ((result = parent.searchVar(name)) != null) {
                return result;
            }
            return parent.searchVar(name);
        }
        return null;
    }

    public StackElement searchUpVal(String name) {
        StackElement temp = upValMap.get(name);
        if (temp != null) {
            return temp;
        }
        temp = searchParentVar(name);
        if (temp != null) {
            upVal.add(temp);
            upValMap.put(name, temp);
        }
        return temp;
    }

    public static Variable searchGlobal(String name) {
        return globalVarMap.get(name);
    }

}
