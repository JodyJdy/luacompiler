package com.jdy.lua.lparser2;

import lombok.Data;

/**
 * 操作数 和 操作数类型
 */
@Data
public class ArgAndKind {
    // kind of operands
    public static final int ARG_CONST = 1; // const index
    public static final int ARG_REG   = 2; // register index
    public static final int ARG_UPVAL = 4; // upvalue index
    public static final int ARG_RK    = ARG_REG | ARG_CONST;
    public static final int ARG_RU    = ARG_REG | ARG_UPVAL;
    private int arg;
    private int kind;

    public ArgAndKind() {
    }

    public ArgAndKind(int arg, int kind) {
        this.arg = arg;
        this.kind = kind;
    }
}
