package com.jdy.lua.lparser2;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lparser2.expr.SubExpr;
import lombok.Getter;

@Getter
public class SubExprWithOp {
    private SubExpr subExpr;
    private BinOpr opr;

    public SubExprWithOp(SubExpr subExpr, BinOpr opr) {
        this.subExpr = subExpr;
        this.opr = opr;
    }


}
