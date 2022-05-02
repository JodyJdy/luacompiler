package com.jdy.lua.lparser2.expr;

import java.util.ArrayList;
import java.util.List;

public class FuncArgs extends Expr{
    private List<Expr> expr1 = new ArrayList<>();
    private StringExpr stringExpr;
    private TableConstructor constructor;

    public FuncArgs(StringExpr stringExpr) {
        this.stringExpr = stringExpr;
    }

    public FuncArgs(TableConstructor constructor) {
        this.constructor = constructor;
    }

    public void addExpr(Expr e){
        expr1.add(e);
    }
}
