package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Statement implements Generator {
    int line;

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        // do nothing
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
