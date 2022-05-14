package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

/**
 * table.key
 */
@Getter
public class TableStrAccess extends Expr{

    private Expr table;
    private StringExpr key;

    public TableStrAccess(Expr table, StringExpr key) {
        this.table = table;
        this.key = key;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
