package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class StringExpr extends Expr {
    private String str;

    public StringExpr(String str) {
        this.str = str;
    }

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
