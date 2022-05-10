package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.FunctionBody;
import com.jdy.lua.lparser2.expr.NameExpr;
import com.jdy.lua.lparser2.expr.ParList;
import lombok.Data;

import java.util.List;

@Data
public class FunctionStat extends Statement{
    /**
     * a.b.c.d:Name(xxx)  fieldDesc用来存储前面的描述
     */
    private List<NameExpr> fieldDesc;
    private NameExpr funcName;
    private FunctionBody functionBody;
    private boolean isMethod;

    public FunctionStat(NameExpr funcName, BlockStatement blockStatement, ParList parList) {
        this.funcName = funcName;
        this.functionBody = new FunctionBody(blockStatement,parList);
        this.functionBody.setMethod(true);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
