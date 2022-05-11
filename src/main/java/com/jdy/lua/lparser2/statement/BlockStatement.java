package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.FunctionInfo;
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

    public void generate(InstructionGenerator ins, FunctionInfo fi){
        ins.generate(this,fi);
    }
}
