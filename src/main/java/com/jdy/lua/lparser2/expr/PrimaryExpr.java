package com.jdy.lua.lparser2.expr;

public class PrimaryExpr extends Expr{
    private NameExpr nameExpr;

    private Expr expr;

    public PrimaryExpr(NameExpr nameExpr) {
        this.nameExpr = nameExpr;
    }

    public PrimaryExpr(Expr expr) {
        this.expr = expr;
    }
}
