package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

/**
 * xxx:a(xx)
 */
@Getter
public class TableMethodCall extends Expr {
    private Expr table;
    private StringExpr method;
    private FuncArgs args;

    public TableMethodCall(Expr table, StringExpr method, FuncArgs args) {
        this.table = table;
        this.method = method;
        this.args = args;
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
