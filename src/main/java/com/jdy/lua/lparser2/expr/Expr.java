package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Expr implements Generator {
    int line;

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
       generator.generate(this,a,n);
    }
}
