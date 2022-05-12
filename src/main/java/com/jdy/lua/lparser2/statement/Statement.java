package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Statement implements Generator {
    int line;

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        // do nothing
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
