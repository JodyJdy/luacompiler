package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class GotoStatement extends Statement{
    private String label;

    public GotoStatement(String label) {
        this.label = label;
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
