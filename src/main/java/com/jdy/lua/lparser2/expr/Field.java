package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
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

    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }
}
