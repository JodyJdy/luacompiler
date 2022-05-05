package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class FloatExpr extends Expr{
    double f;

    public FloatExpr(double f) {
        this.f = f;
    }
    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
