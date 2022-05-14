package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExprStatement extends Statement{

    private List<Expr> lefts = new ArrayList<>();
    private List<Expr> rights;
    private Expr func;


    public ExprStatement() {
    }

    public void addLeft(Expr expr){
        lefts.add(expr);
    }
    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
