package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lparser2.expr.Expr;
import com.jdy.lua.lparser2.expr.ExprList;
import com.jdy.lua.lparser2.expr.NameExpr;

public class ForStatement extends Statement{
    boolean isGeneric;

    private NameExpr name1;
    private NameExpr name2;
    private Expr expr1;
    private Expr expr2;
    private Expr expr3;
    private ExprList exprList;

    private BlockStatement block;

    public ForStatement(NameExpr name1, Expr expr1, Expr expr2, Expr expr3) {
        this.name1 = name1;
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.expr3 = expr3;
        isGeneric = false;
    }
    public ForStatement(NameExpr name1,NameExpr name2,ExprList exprList){
        this.name1 = name1;
        this.name2 = name2;
        this.exprList = exprList;

    }

}
