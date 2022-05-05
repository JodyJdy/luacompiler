package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class TrueExpr extends Expr{

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
