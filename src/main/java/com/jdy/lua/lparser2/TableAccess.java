package com.jdy.lua.lparser2;

import com.jdy.lua.lparser2.expr.Expr;
import lombok.Getter;

@Getter
public class TableAccess {

    private Expr table;
    private Expr key;

    public TableAccess(Expr table, Expr key) {
        this.table = table;
        this.key = key;
    }
}
