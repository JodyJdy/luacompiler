package com.jdy.lua.lparser2.expr;

public class TableIndex extends Expr{
    private Expr expr;

    public TableIndex(Expr expr) {
        this.expr = expr;
    }
}
