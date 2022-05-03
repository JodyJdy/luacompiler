package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import lombok.Data;

import static com.jdy.lua.lcodes.BinOpr.OPR_NOBINOPR;

@Data
public class SubExpr extends Expr{

    private UnOpr unOpr;
    private Expr subExpr1;
    private BinOpr binOpr = OPR_NOBINOPR;
    private SubExpr subExpr2;

    public SubExpr(Expr subExpr1) {
        this.subExpr1 = subExpr1;
    }

    public SubExpr(UnOpr unOpr, SubExpr subExpr) {
        this.unOpr = unOpr;
        this.subExpr1 = subExpr;
    }
}
