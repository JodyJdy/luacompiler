package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.statement.BlockStatement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionBody  extends Expr{
    private BlockStatement block;
    private ParList parList;
    private boolean isMethod = false;

    public FunctionBody(BlockStatement block,ParList parList) {
        this.block = block;
        this.parList = parList;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }

}
