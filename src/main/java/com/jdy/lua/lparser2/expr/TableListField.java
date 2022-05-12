package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class TableListField extends Expr{
    private Expr expr;

    public TableListField(Expr expr) {
        this.expr = expr;
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}

