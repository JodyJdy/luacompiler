package com.jdy.lua.executor;

import com.jdy.lua.data.Value;
import com.jdy.lua.statement.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jdy
 * @title: Block
 * @description:
 * @data 2023/9/14 16:34
 */
public class Block {
    /**
     * block对应的blockStatement
     */
    private Statement.BlockStatement blockStatement;
    /**
     *
     * 父级 Block,如果是函数的话， parent可能是另一个函数
     */
    protected Block parent;
    /**
     * 全局变量
     */
    protected static final Map<String,Variable> globalVariableMap = new HashMap<>();
    /**
     * block 内部的局部变量
     */
    protected final Map<String,Variable> localVariableMap = new HashMap<>();

    /**
     * 根据 变量名称搜索 变量
     * @param name
     * @return
     */
    public Variable searchVariable(String name) {
        //当前block如果有局部变量
        if (localVariableMap.containsKey(name)) {
            return localVariableMap.get(name);
        }
        //从父类搜索变量
        if (parent != null) {
            return parent.searchVariable(name);
        }

        //返回全局变量
        if (globalVariableMap.containsKey(name)) {
            return globalVariableMap.get(name);
        }
        throw new RuntimeException("变量不存在:" + name);
    }


    public void addGlobalVar(String name, Value value) {
        globalVariableMap.put(name, new Variable(name, value));
    }
    public void addGlobalVar(Variable var) {
        globalVariableMap.put(var.getName(), var);
    }
}
