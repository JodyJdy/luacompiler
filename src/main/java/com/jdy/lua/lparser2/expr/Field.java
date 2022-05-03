package com.jdy.lua.lparser2.expr;

import lombok.Data;

@Data
public class Field extends Expr{
    private TableField tableField;
    private TableListField tableListField;

    public Field(TableField tableField) {
        this.tableField = tableField;
    }

    public Field(TableListField tableListField) {
        this.tableListField = tableListField;
    }
}
