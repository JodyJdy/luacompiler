package com.jdy.lua.executor;

import com.jdy.lua.data.Function;
import com.jdy.lua.data.Value;

public class Iterator {
    /**
     * 迭代的函数
     */
    private Function iteratorFunc;
    /**
     * 被迭代的对象
     */
    private Value source;

    /**
     * 当前迭代的下标
     */
    private Value var;

    public Function getIteratorFunc() {
        return iteratorFunc;
    }

    public void setIteratorFunc(Function iteratorFunc) {
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

    public Iterator(Function iteratorFunc, Value source, Value var) {
        this.iteratorFunc = iteratorFunc;
        this.source = source;
        this.var = var;
    }
}
