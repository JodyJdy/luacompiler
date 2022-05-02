package com.jdy.lua.lparser2.expr;

public class SimpleExpr extends Expr{
    private Expr expr;

    public SimpleExpr(Expr expr) {
        this.expr = expr;
    }
}
