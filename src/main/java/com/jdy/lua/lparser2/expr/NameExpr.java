package com.jdy.lua.lparser2.expr;

public class NameExpr extends Expr{
    private String name;


    public NameExpr(String name) {
        this.name = name;
    }
}
