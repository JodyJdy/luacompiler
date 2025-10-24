package com.jdy.lua.executor;

import com.jdy.lua.data.LuaFunction;
import com.jdy.lua.data.Value;

public class Iterator {
    /**
     * 迭代的函数
     */
    private LuaFunction iteratorFunc;
    /**
     * 被迭代的对象
     */
    private Value source;

    /**
     * 当前迭代的下标
     */
    private Value var;

    public LuaFunction getIteratorFunc() {
        return iteratorFunc;
    }

    public void setIteratorFunc(LuaFunction iteratorFunc) {
        this.iteratorFunc = iteratorFunc;
    }

    public Value getSource() {
        return source;
    }

    public void setSource(Value source) {
        this.source = source;
    }

    public Value getVar() {
        return var;
    }

    public void setVar(Value var) {
        this.var = var;
    }

    public Iterator(LuaFunction iteratorFunc, Value source, Value var) {
        this.iteratorFunc = iteratorFunc;
        this.source = source;
        this.var = var;
    }
}
