package com.jdy.lua.data;

import com.jdy.lua.executor.Block;
import com.jdy.lua.statement.Expr;
import lombok.Data;

@Data
public class Function implements Value{
    private final Block parent;
    private  final Expr.FunctionBody body;

    public Function(Block parent,Expr.FunctionBody body) {
        this.parent = parent;
        this.body = body;
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.FUNCTION;
    }

    @Override
    public String toString() {
        return this.body.getNames().toString();
    }
}
