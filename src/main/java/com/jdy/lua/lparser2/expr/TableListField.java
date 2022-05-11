package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class TableListField extends Expr{
    private Expr expr;

    public TableListField(Expr expr) {
        this.expr = expr;
    }
    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }

}

