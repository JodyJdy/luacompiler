package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Expr implements Generator {
    int line;

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        // do nothing
    }
}
