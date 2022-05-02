package com.jdy.lua.lparser2.expr;

public class SuffixedExp extends Expr{

    private NameExpr e;
    private FuncArgs funcArgs;
    private Expr expr;
    private boolean hasDot;
    private boolean hasColon;

    public SuffixedExp(NameExpr e) {
        this.e = e;
        this.hasDot = true;
    }

    public SuffixedExp(Expr expr) {
        this.expr = expr;
    }

    public SuffixedExp(NameExpr e, FuncArgs funcArgs) {
        this.e = e;
        this.funcArgs = funcArgs;
        this.hasColon = true;
    }

    public SuffixedExp(FuncArgs funcArgs) {
        this.funcArgs = funcArgs;
    }
}
