package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

/**
 * a(xxx) 或者 a.b.c(xx)
 */
@Getter
public class FuncCall extends Expr {
    private Expr func;
    private FuncArgs args;

    public FuncCall(Expr func, FuncArgs args) {
        this.func = func;
        this.args = args;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
