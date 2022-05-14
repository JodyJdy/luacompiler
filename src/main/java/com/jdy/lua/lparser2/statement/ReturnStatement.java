package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;
import lombok.Getter;

import java.util.List;

@Getter
public class ReturnStatement extends Statement{
    List<Expr> exprList;

    public ReturnStatement(List<Expr> exprList) {
        this.exprList = exprList;
    }
    public ReturnStatement(){

    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
