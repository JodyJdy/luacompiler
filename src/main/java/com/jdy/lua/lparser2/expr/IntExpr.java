package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class IntExpr extends Expr{
    private long i;

    public IntExpr(long i) {
        this.i = i;
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }

}
