package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
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
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator, GenerateInfo info) {
        return generator.generate(this, info);
    }
}
