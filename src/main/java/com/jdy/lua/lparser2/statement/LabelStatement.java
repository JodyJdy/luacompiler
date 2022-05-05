package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.NameExpr;

public class LabelStatement extends Statement{
    private NameExpr nameExpr;

    public LabelStatement(NameExpr nameExpr) {
        this.nameExpr = nameExpr;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
