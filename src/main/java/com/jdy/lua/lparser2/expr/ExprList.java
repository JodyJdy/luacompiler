package com.jdy.lua.lparser2.expr;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExprList extends Expr{
    private List<Expr> exprList = new ArrayList<>();

    public ExprList() {
    }

    public void addExpr(Expr e){
        exprList.add(e);
    }
}
