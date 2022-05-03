package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lparser2.statement.BlockStatement;

public class FunctionBody  extends Expr{
    private BlockStatement block;

    public FunctionBody(BlockStatement block) {
        this.block = block;
    }
}
