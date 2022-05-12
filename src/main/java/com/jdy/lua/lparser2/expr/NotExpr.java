package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;

public class NotExpr extends LogicExpr{

    public NotExpr(Expr left, Expr right, BinOpr op) {
        super(left,null,null);
    }
}
