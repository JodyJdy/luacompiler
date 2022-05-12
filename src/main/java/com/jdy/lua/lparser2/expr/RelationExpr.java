package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;

public class RelationExpr extends LogicExpr{

    public RelationExpr(Expr left, Expr right, BinOpr op) {
        super(left, right, op);
    }
}
