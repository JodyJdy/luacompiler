package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.Expr;

public class RepeatStatement extends Statement{
    private BlockStatement block;
    private Expr cond;

    public RepeatStatement(BlockStatement block, Expr cond) {
        this.block = block;
        this.cond = cond;
    }
}
