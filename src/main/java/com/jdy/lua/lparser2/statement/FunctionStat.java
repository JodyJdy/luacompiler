package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.NameExpr;
import lombok.Data;

import java.util.List;

@Data
public class FunctionStat extends Statement{
    /**
     * a.b.c.d:Name(xxx)  fieldDesc用来存储前面的描述
     */
    private List<NameExpr> fieldDesc;
    private NameExpr funcName;
    private BlockStatement blockStatement;
    private boolean isMethod;

    public FunctionStat(NameExpr funcName, BlockStatement blockStatement) {
        this.funcName = funcName;
        this.blockStatement = blockStatement;
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
