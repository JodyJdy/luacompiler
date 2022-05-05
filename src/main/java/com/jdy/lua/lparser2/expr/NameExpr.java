package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class NameExpr extends Expr{
    private String name;


    public NameExpr(String name) {
        this.name = name;
    }
    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
