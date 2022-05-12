package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class LogicExpr extends Expr {

    private Expr left;
    private Expr right;
    /**
     * and,or
     */
    private BinOpr op;

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        super.generate(generator, a, n);
    }

    public LogicExpr(Expr left, Expr right, BinOpr op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }
}
