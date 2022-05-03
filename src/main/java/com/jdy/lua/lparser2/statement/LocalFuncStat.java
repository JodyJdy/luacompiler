package com.jdy.lua.lparser2.statement;

public class LocalFuncStat extends Statement{
    private String str;
    private BlockStatement block;

    public LocalFuncStat(String str, BlockStatement block) {
        this.str = str;
        this.block = block;
    }
}
