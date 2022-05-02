package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.ExprList;
import com.jdy.lua.lparser2.expr.NameExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalStatement extends Statement{
    List<NameExpr> nameExprList = new ArrayList<>();
    Map<Integer,String> attributes = new HashMap<>();
    private ExprList exprList;
}
