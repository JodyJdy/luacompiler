package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class StringExpr extends Expr {
    private String str;

    public StringExpr(String str) {
        this.str = str;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }

}
