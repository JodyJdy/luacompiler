package com.jdy.lua.lparser2.expr;

import lombok.Data;

@Data
public class SuffixedExp extends Expr{

    private Expr primaryExr;
    private NameExpr nameExpr;
    private FuncArgs funcArgs;
    private Expr expr;
    private boolean hasDot;
    private boolean hasColon;



    public SuffixedExp(Expr primaryExr) {
        this.primaryExr = primaryExr;
    }

}
