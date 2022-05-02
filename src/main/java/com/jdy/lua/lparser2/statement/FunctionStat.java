package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.NameExpr;

public class FunctionStat extends Statement{
    private NameExpr funcName;
    private BlockStatement blockStatement;

    public FunctionStat(NameExpr funcName, BlockStatement blockStatement) {
        this.funcName = funcName;
        this.blockStatement = blockStatement;
    }
}
