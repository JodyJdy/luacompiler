package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class BlockStatement extends Statement{
    private StatList statList;

    public BlockStatement(StatList statList) {
        this.statList = statList;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
