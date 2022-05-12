package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class FloatExpr extends Expr{
    double f;

    public FloatExpr(double f) {
        this.f = f;
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }

}
