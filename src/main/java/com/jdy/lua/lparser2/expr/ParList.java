package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParList extends Expr{
    private List<NameExpr> nameExprs = new ArrayList<>();
    private boolean hasVararg = false;


    public void addNameExpr(NameExpr ex){
        nameExprs.add(ex);
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
