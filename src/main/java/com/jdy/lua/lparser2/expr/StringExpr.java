package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class StringExpr extends Expr {
    private String str;

    public StringExpr(String str) {
        this.str = str;
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
