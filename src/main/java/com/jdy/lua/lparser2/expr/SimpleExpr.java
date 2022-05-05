package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class SimpleExpr extends Expr{
    private Expr expr;

    public SimpleExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator, GenerateInfo info) {
        return generator.generate(this,info);
    }

}
