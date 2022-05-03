package com.jdy.lua.lparser2.expr;

public class TableField extends Expr{
    private Expr left;
    private Expr right;

    public TableField(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }
}
