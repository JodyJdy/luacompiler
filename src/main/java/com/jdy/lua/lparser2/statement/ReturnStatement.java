package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.ExprList;
import lombok.Getter;

@Getter
public class ReturnStatement extends Statement{
    ExprList exprList;

    public ReturnStatement(ExprList exprList) {
        this.exprList = exprList;
    }
    public ReturnStatement(){

    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
