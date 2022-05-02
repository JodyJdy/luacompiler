package com.jdy.lua.lparser2.expr;

public class TableListField extends Expr{
    private Expr expr;

    public TableListField(Expr expr) {
        this.expr = expr;
    }
}

