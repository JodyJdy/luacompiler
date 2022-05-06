package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;

public class RepeatStatement extends Statement{
    private BlockStatement block;
    private Expr cond;

    public RepeatStatement(BlockStatement block, Expr cond) {
        this.block = block;
        this.cond = cond;
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }
}
