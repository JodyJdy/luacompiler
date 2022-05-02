package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lparser2.statement.StatList;

public class FuncBodyExpr extends Expr {
    StatList statList;

    public FuncBodyExpr(StatList statList) {
        this.statList = statList;
    }
}
