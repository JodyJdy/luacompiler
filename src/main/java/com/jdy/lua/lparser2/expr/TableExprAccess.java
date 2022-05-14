package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

/**
 * table[expr]
 */
@Getter
public class TableExprAccess extends Expr {

    private Expr table;
    private Expr key;

    public TableExprAccess(Expr table, Expr key) {
        this.table = table;
        this.key = key;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
