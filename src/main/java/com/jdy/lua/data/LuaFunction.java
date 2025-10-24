package com.jdy.lua.data;

import com.jdy.lua.executor.Block;
import com.jdy.lua.statement.Expr;
import lombok.Data;

import java.util.Objects;

@Data
public class LuaFunction implements Value {
    /**
     * 是否是原生的
     */
    protected boolean isNative = false;

    private final Block parent;
    private final Expr.LuaFunctionBody body;
    /*
    是否是对象方法 a:b
     */
    private boolean objMethod =false;

    public LuaFunction(Block parent, Expr.LuaFunctionBody body, boolean objMethod) {
        this(parent, body);
        this.objMethod = objMethod;
    }

    public LuaFunction(Block parent, Expr.LuaFunctionBody body) {
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

    @Override
    public BoolValue eq(Value b) {
        if (b instanceof LuaFunction f) {
           if(f.objMethod == this. objMethod
                   && this.isNative == f.isNative
                    && this.body.equals(f.body)
                   && Objects.equals(this.parent,f.parent)){
              return BoolValue.TRUE;
           }
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        return eq(b) == BoolValue.TRUE ? BoolValue.FALSE : BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        throw new RuntimeException("Function无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        throw new RuntimeException("Function无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        throw new RuntimeException("Function无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        throw new RuntimeException("Function无法比较大小");
    }
}
