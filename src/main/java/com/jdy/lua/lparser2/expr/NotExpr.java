package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class NotExpr extends LogicExpr{

    public NotExpr(Expr left) {
        super(left,null,null);
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
