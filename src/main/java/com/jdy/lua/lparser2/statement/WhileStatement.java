package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;

public class WhileStatement extends Statement {
    private Expr cond;
    private BlockStatement block;

    public WhileStatement(Expr cond, BlockStatement block) {
        this.cond = cond;
        this.block = block;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
