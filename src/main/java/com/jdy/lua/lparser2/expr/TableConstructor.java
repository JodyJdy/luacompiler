package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.ExprDesc;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TableConstructor  extends Expr{
    // [x]=x
    private List<TableField> fields = new ArrayList<>();
    // x,y,z
    private List<TableListField> listFields = new ArrayList<>();

    public void addTableFields(TableField field){
        fields.add(field);
    }
    public void addTableListFieds(TableListField listField){
        listFields.add(listField);
    }
    @Override
    public void generate(InstructionGenerator generator, ExprDesc exprDesc) {
        generator.generate(this,exprDesc);
    }

}
