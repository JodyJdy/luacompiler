package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
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
    public DataTypeEnum type() {
        return DataTypeEnum.BOOLEAN;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(b);
    }
}
