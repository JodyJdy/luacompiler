package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.statement.BlockStatement;

public class FunctionBody  extends Expr{
    private BlockStatement block;

    public FunctionBody(BlockStatement block) {
        this.block = block;
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }

}
