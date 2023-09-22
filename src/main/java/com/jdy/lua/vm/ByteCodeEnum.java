package com.jdy.lua.vm;

/**
 * @author jdy
 * @title: ByteCodeEnum
 * @description:
 * @data 2023/9/22 15:24
 */
public enum ByteCodeEnum {
    Jmp, Add, Sub, MUL, Div, Mod, IntMod, Pow,
    BitAnd, BitOr, BitLeftShift, BitRightShift, Cat,
    And, Or, Test, Eq, Ne, Ge, Gt, Lt, Le, LoadVar,
    LoadUpVar, SaveNil, LoadFunc, LoadConstant,
    LoadGlobal, SaveVar, SaveGlobal, SaveUpval, Call,
    VarArgs, ReturnMulti, Return, GetTableMethod, GetTable,
    NewTable, SetTableNil, SetTable, SetTableArray, NumberFor,
    EndNumberFor, GenericFor, Length, Not, BitReverse,
    Negative, LoadGlobalModule, LoadModule
}
