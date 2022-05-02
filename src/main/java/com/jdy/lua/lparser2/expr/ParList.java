package com.jdy.lua.lparser2.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParList extends Expr{
    private List<NameExpr> nameExprs = new ArrayList<>();
    private boolean hasVararg = false;

    public ParList(boolean hasVararg) {
        this.hasVararg = hasVararg;
    }
    public void addNameExpr(NameExpr ex){
        nameExprs.add(ex);
    }
}
