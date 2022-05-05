package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

@Getter
public class NameExpr extends Expr{
    private String name;


    public NameExpr(String name) {
        this.name = name;
    }
    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator, GenerateInfo info) {
        return generator.generate(this,info);
    }

}
