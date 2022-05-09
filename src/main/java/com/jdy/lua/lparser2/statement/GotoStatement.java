package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.NameExpr;
import com.jdy.lua.lparser2.expr.StringExpr;

import javax.naming.Name;

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
