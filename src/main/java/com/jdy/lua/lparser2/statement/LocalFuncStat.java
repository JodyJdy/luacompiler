package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class LocalFuncStat extends Statement{
    private String str;
    private BlockStatement block;

    public LocalFuncStat(String str, BlockStatement block) {
        this.str = str;
        this.block = block;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
