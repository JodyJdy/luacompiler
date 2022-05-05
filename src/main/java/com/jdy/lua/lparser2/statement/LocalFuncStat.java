package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class LocalFuncStat extends Statement{
    private String str;
    private BlockStatement block;

    public LocalFuncStat(String str, BlockStatement block) {
        this.str = str;
        this.block = block;
    }

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
