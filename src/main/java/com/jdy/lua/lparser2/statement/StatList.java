package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StatList extends Statement {
    private List<Statement> statements = new ArrayList<>();


    public void addStatement(Statement s){
        statements.add(s);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }
}
