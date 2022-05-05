package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class SimpleExpr extends Expr{
    private Expr expr;

    public SimpleExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
