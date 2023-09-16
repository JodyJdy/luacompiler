package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;

/**
 * @author jdy
 * @title: NilValue
 * @description:
 * @data 2023/9/14 16:29
 */
public class NilValue implements Value, Expr {
    public static NilValue NIL = new NilValue();
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.NIL;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return this;
    }

    @Override
    public String toString() {
        return "nil";
    }
}
