package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.UnOpr;

public class UnaryExpr extends Expr {
    private UnOpr opr;
    private Expr expr;

    public UnaryExpr(UnOpr opr, Expr expr) {
        this.opr = opr;
        this.expr = expr;
    }
}
