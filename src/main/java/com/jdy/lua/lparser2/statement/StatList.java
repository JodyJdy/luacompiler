package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;

import java.util.ArrayList;
import java.util.List;

public class StatList extends Statement {
    private List<Statement> statements = new ArrayList<>();


    public void addStatement(Statement s){
        statements.add(s);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }
}
