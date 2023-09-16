package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import lombok.Data;

/**
 * @author jdy
 * @title: NumberValue
 * @description:
 * @data 2023/9/14 16:29
 */
@Data
public class NumberValue implements Value, Expr {

    private Float f;

    public NumberValue(Float f) {
        this.f = f;
    }

    public NumberValue(int i) {
        this.f = (float) i;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return new NumberValue(f);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.NUMBER;
    }

    @Override
    public String toString() {
        return String.valueOf(f);
    }
}
