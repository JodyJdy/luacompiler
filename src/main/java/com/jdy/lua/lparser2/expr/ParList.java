package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParList extends Expr{
    private List<NameExpr> nameExprs = new ArrayList<>();
    private boolean hasVararg = false;

    public ParList(boolean hasVararg) {
        this.hasVararg = hasVararg;
    }
    public void addNameExpr(NameExpr ex){
        nameExprs.add(ex);
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }
}
