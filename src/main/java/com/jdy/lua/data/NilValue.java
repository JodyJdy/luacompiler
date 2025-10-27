package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.ExprTypeEnum;

/**
 * @author jdy
 * @title: NilValue
 * @description:
 * @data 2023/9/14 16:29
 */
public class NilValue implements Value, Expr {
    public static final NilValue NIL = new NilValue();

    private NilValue() {

    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.NIL;
    }

    @Override
    public ExprTypeEnum exprType() {
        return  ExprTypeEnum.NILValue;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return this;
    }

    @Override
    public String toString() {
        return "nil";
    }

    @Override
    public BoolValue eq(Value b) {
        if (b == NIL) {
            return BoolValue.TRUE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        if (b != NIL) {
            return BoolValue.TRUE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue lt(Value b) {
        throw new RuntimeException("空值无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        throw new RuntimeException("空值无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        throw new RuntimeException("空值无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        throw new RuntimeException("空值无法比较大小");
    }
}

