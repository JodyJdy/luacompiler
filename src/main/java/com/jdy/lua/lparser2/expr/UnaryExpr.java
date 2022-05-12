package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.UnOpr;
import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class UnaryExpr extends Expr {
    private UnOpr opr;
    private Expr expr;

    public UnaryExpr(UnOpr opr, Expr expr) {
        this.opr = opr;
        this.expr = expr;
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
