package com.jdy.lua.executor;

import com.jdy.lua.data.Value;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jdy
 * @title: Function
 * @description:
 * @data 2023/9/14 16:33
 */
public class Function extends Block {
    /**
     * 函数所对应的 FunctionBody
     */
    private Expr.FunctionBody functionBody;
    /**
     * 入参的值
     */
    private final Map<String, Variable> args = new HashMap<>();




    public Function(List<Value> args) {
        Statement.FunctionStatement fs;
    }

    @Override
    public Variable searchVariable(String name) {
        //当前 如果有局部变量
        if (localVariableMap.containsKey(name)) {
            return localVariableMap.get(name);
        }
        if (args.containsKey(name)) {
            return args.get(name);
        }
        // 从函数参数里面搜索
        if (parent != null) {
            return parent.searchVariable(name);
        }
        if (globalVariableMap.containsKey(name)) {
            return globalVariableMap.get(name);
        }
        throw new RuntimeException("变量不存在:" + name);
    }
}
