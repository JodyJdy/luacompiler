package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Expr implements Generator {
    int line;

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
        return null;
    }

    public GenerateInfo generate(InstructionGenerator generator,GenerateInfo info){
        return null;
    }
}
