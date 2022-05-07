package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FuncArgs extends Expr{
    private List<Expr> expr1 = new ArrayList<>();
    private StringExpr stringExpr;
    private TableConstructor constructor;

    public FuncArgs(StringExpr stringExpr) {
        this.stringExpr = stringExpr;
    }

    public FuncArgs(TableConstructor constructor) {
        this.constructor = constructor;
    }
    public FuncArgs(){

    }

    public void addExprList(ExprList e){
        expr1.addAll(e.getExprList());
    }
    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }

}
