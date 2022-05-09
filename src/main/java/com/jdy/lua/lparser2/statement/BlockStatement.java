package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class BlockStatement extends Statement{
    private StatList statList;

    public BlockStatement(StatList statList) {
        this.statList = statList;
    }

    @Override
    public void generate(InstructionGenerator ins) {
       ins.generate(this);
    }
}
