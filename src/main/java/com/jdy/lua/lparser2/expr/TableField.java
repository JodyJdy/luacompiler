package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class TableField extends Expr{
    private Expr left;
    private Expr right;

    public TableField(Expr left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }

}
