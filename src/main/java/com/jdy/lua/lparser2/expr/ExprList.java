package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ExprList extends Expr{
    private List<Expr> exprList = new ArrayList<>();

    public ExprList() {
    }

    public void addExpr(Expr e){
        exprList.add(e);
    }

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
