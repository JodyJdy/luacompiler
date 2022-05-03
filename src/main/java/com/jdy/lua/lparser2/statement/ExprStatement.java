package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.Expr;
import com.jdy.lua.lparser2.expr.ExprList;
import com.jdy.lua.lparser2.expr.SuffixedExp;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExprStatement extends Statement{

    private List<SuffixedExp> lefts = new ArrayList<>();
    private ExprList right;
    private SuffixedExp func;


    public ExprStatement() {
    }

    public void addLeft(SuffixedExp suffixedExp){
        lefts.add(suffixedExp);
    }
}
