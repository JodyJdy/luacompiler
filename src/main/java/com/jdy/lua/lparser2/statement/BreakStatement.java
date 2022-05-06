package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class BreakStatement extends Statement{

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }
}
