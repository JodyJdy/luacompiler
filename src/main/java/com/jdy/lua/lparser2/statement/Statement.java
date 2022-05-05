package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.Generator;
import com.jdy.lua.lcodes2.InstructionGenerator;

public abstract class Statement implements Generator {
    int line;

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {

        return generator.generate(this);
    }
}
