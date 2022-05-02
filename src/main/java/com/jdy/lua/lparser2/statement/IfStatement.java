package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.Expr;

import java.util.ArrayList;
import java.util.List;


public class IfStatement extends Statement{
    private Expr cond;
    private BlockStatement blockStatement;

    private List<Expr> elseThenConds = new ArrayList<>();
    private List<BlockStatement> elseThenBlock = new ArrayList<>();

    private BlockStatement elseBlock;


    public IfStatement(Expr cond, BlockStatement blockStatement) {
        this.cond = cond;
        this.blockStatement = blockStatement;
    }
}
