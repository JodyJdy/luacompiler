package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

public class TrueExpr extends Expr{

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
