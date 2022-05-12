package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class RelationExpr extends LogicExpr{

    public RelationExpr(Expr left, Expr right, BinOpr op) {
        super(left, right, op);
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
