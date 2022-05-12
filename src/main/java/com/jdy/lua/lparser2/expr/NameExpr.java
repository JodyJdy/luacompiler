package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class NameExpr extends Expr{
    private String name;


    public NameExpr(String name) {
        this.name = name;
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
