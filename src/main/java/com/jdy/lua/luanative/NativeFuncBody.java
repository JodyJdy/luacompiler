package com.jdy.lua.luanative;

import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;
import lombok.Getter;

import java.util.List;

/**
 * 原生方法结构体
 */
@Getter
public class NativeFuncBody extends Expr.Function {
    private final java.util.function.Function<List<Value>,Value> execute;

    public NativeFuncBody(List<String> paramNames,java.util.function.Function<List<Value>,Value> execute) {
        this.paramNames = paramNames;
        this.execute = execute;
    }

    public NativeFuncBody(List<String> paramNames, boolean hasMulti,java.util.function.Function<List<Value>,Value> execute ) {
        this(paramNames,execute);
        this.hasMultiArg = hasMulti;
    }
    @Override
    public Statement.BlockStatement getBlockStatement() {
        // 不可能到达这里
        throw new RuntimeException("不允许的调用");
    }
    @Override
    public Value visitExpr(Executor visitor) {
        // 不可能到达这里
        throw new RuntimeException("不允许的调用");
    }
}
