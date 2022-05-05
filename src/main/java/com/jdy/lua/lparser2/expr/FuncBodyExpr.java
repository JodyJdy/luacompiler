package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.statement.StatList;

public class FuncBodyExpr extends Expr {
    StatList statList;

    public FuncBodyExpr(StatList statList) {
        this.statList = statList;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator, GenerateInfo info) {
        return generator.generate(this,info);
    }

}
