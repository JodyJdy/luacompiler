package com.jdy.lua.data;

import com.jdy.lua.executor.Block;
import com.jdy.lua.statement.Expr;
import lombok.Data;

@Data
public class Function implements Value {
    /**
     * 是否是原生的
     */
    protected boolean isNative = false;

    private final Block parent;
    private final Expr.Function body;
    /*
    是否是对象方法 a:b
     */
    private boolean objMethod =false;

    public Function(Block parent, Expr.Function body,boolean objMethod) {
        this(parent, body);
        this.objMethod = objMethod;
    }

    public Function(Block parent, Expr.Function body) {
        this.parent = parent;
        this.body = body;
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.FUNCTION;
    }

    @Override
    public String toString() {
        return "function";
    }
}
