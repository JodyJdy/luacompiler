package com.jdy.lua.lparser2.statement;

public class BlockStatement extends Statement{
    private StatList statList;

    public BlockStatement(StatList statList) {
        this.statList = statList;
    }
}
