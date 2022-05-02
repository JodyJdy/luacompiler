package com.jdy.lua.lparser2.statement;

import java.util.ArrayList;
import java.util.List;

public class StatList implements Statement {
    private List<Statement> statements = new ArrayList<>();


    public void addStatement(Statement s){
        statements.add(s);
    }
}
