package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import lombok.Data;

@Data
public class SubExpr extends Expr{
    private SimpleExpr simpleExpr;
    private UnOpr unOpr;
    private SubExpr subExpr;
    private BinOpr binOpr;
    private SubExpr subExpr2;

    public SubExpr(SimpleExpr simpleExpr) {
        this.simpleExpr = simpleExpr;
    }

    public SubExpr(UnOpr unOpr, SubExpr subExpr) {
        this.unOpr = unOpr;
        this.subExpr = subExpr;
    }
}
