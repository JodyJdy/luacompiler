package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import lombok.Data;

@Data
public class BinaryExpr extends Expr {
    private Expr left;
    private Expr right;
    private BinOpr op;

    public BinaryExpr(Expr left, Expr right, BinOpr op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }
}
