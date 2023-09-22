package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.ExprTypeEnum;
import lombok.Data;

/**
 * @author jdy
 * @title: BoolValue
 * @description:
 * @data 2023/9/14 16:28
 */
@Data
public class BoolValue implements Value, Expr {
    public static BoolValue TRUE = new BoolValue(true);
    public static BoolValue FALSE = new BoolValue(false);
    private BoolValue(Boolean b) {
        this.b = b;
    }

    Boolean b;

    @Override
    public ExprTypeEnum exprType() {
        return  ExprTypeEnum.BoolValue;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.BOOLEAN;
    }

    @Override
    public BoolValue eq(Value b) {
        return this.equals(b) ? TRUE : FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        return this.equals(b) ? FALSE : TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        throw new RuntimeException("布尔值无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        throw new RuntimeException("布尔值无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        throw new RuntimeException("布尔值无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        throw new RuntimeException("布尔值无法比较大小");
    }
}
