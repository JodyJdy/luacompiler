package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class FloatExpr extends Expr{
    double f;

    public FloatExpr(double f) {
        this.f = f;
    }
    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
