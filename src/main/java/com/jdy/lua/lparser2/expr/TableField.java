package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;

public class TableField extends Expr{
    private Expr left;
    private Expr right;

    public TableField(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void generate(InstructionGenerator generator) {
        generator.generate(this);
    }
}
