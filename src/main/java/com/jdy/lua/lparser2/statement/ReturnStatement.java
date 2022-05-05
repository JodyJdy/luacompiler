package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.ExprList;

public class ReturnStatement extends Statement{
    ExprList exprList;

    public ReturnStatement(ExprList exprList) {
        this.exprList = exprList;
    }
    public ReturnStatement(){

    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
