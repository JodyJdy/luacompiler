package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class IntExpr extends Expr{
    private long i;

    public IntExpr(long i) {
        this.i = i;
    }

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
