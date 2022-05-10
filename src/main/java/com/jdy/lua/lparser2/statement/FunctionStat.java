package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.FunctionBody;
import com.jdy.lua.lparser2.expr.NameExpr;
import com.jdy.lua.lparser2.expr.ParList;
import com.jdy.lua.lparser2.expr.StringExpr;
import lombok.Data;

import java.util.List;

@Data
public class FunctionStat extends Statement{

    /**
     * a.b.c.d:xx()
     * 那么 NameExpr是 a
     * fielDesc是 b.c.d.xx
     *
     * xx()
     * 那么NameExpr是xx
     *
     * var存放函数定义中的第一个变量，其他的全是常量
     */
    private List<StringExpr> fieldDesc;
    private NameExpr var;
    private FunctionBody functionBody;
    private boolean isMethod;

    public FunctionStat(NameExpr funcName, BlockStatement blockStatement, ParList parList) {
        this.var = funcName;
        this.functionBody = new FunctionBody(blockStatement,parList);
        this.functionBody.setMethod(true);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
