package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.FunctionBody;
import com.jdy.lua.lparser2.expr.ParList;
import lombok.Getter;

@Getter
public class LocalFuncStat extends Statement{
    private String str;
    private FunctionBody functionBody;

    public LocalFuncStat(String str,ParList parList, BlockStatement block) {
        this.str = str;
        this.functionBody = new FunctionBody(block, parList);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
