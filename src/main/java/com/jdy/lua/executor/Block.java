package com.jdy.lua.executor;

import com.jdy.lua.data.Value;
import com.jdy.lua.statement.Statement;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jdy
 * @title: Block
 * @description:
 * @data 2023/9/14 16:34
 */
@Data
public class Block {
    /**
     * block对应的blockStatement
     */
    private Statement.BlockStatement blockStatement;
    /**
     *
     * 父级 Block
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



    public Block(Statement.BlockStatement blockStatement) {
        this.blockStatement = blockStatement;
    }
    public Block(Block parent) {
        this.parent = parent;
    }
    public Block(Block parent,Statement.BlockStatement blockStatement) {
        this.parent = parent;
        this.blockStatement = blockStatement;
    }

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
        //变量不存在
        return null;
    }

    public void removeVar(String name) {
        this.localVariableMap.remove(name);
    }
    public void addVar(String name, Value value) {
        this.localVariableMap.put(name, new Variable(name, value));
    }
    public void addGlobalVar(String name, Value value) {
        globalVariableMap.put(name, new Variable(name, value));
    }
    public void addGlobalVar(Variable var) {
        globalVariableMap.put(var.getName(), var);
    }

    public static void addNative(String name, Value value) {
        globalVariableMap.put(name, new Variable(name,value));
    }
}
