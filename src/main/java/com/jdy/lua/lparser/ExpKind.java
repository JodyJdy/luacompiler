package com.jdy.lua.lparser;

/***
 * 表达式类型枚举
 */
public enum ExpKind {



    /** expdesc作为列表中最后一个表达式时， 表示空的*/
    VVOID(0),  /** when 'expdesc' describes the last expression of a list,
             this kind means an empty list (so, no expression) */
    //一些常量
    VNIL(1),  /** constant nil */
    VTRUE(2),  /** constant true */
    VFALSE(3),  /** constant false */
    VK(4),  /** constant in 'constants'; info = indexForTable of constant in 'constants' */
    VKFLT(5),  /** floating constant; nval = numerical float value */
    VKINT(6),  /** integer constant; ival = numerical integer value */
    VKSTR(7),  /** string constant; strval = TString address;
             (string is fixed by the lexer) */
    // 值存储在固定寄存器的 表达式
    VNONRELOC(8),  /** expression has its value in a fixed register;
                 info = result register */
    // local 变量
    VLOCAL(9),  /** local variable; var.registerIndex = register indexForTable;
              var.actVarIndex = relative indexForTable in 'actvar.arr'  */
    // upvalue 变量
    VUPVAL(10),  /** upvalue variable; info = indexForTable of upvalue in 'upvalues' */
    VCONST(11),  /** compile-time <const> variable;
              info = absolute indexForTable in 'actvar.arr'  */
    VINDEXED(12),  /** indexed variable;
                ind.currTk = table register;
                ind.indexForTable = key's R indexForTable */
    VINDEXUP(13),  /** indexed upvalue;
                ind.currTk = table upvalue;
                ind.indexForTable = key's K indexForTable  */
    VINDEXI(14), /** indexed variable with constant integer;
                ind.currTk = table register;
                ind.indexForTable = key's value */
    VINDEXSTR(15), /** indexed variable with literal string;
                ind.currTk = table register;
                ind.indexForTable = key's K indexForTable */
    VJMP(16),  /** expression is a test/comparison;  测试/比较 表达式
            info = pc of corresponding jump instruction */
    VRELOC(17),  /** expression can put result in any register;  可以将结果放在任意寄存器的表达式
             表达式需要返回值
              info = instruction pc */
    VCALL(18),  /** expression is a function call; info = instruction pc    函数调用*/
    VVARARG(19);  /** vararg expression; info = instruction pc   变长参数 变量*/

    int kind;
    ExpKind(int k){
        this.kind = k;
    }

    public static ExpKind getExpKind(int k){
        for(ExpKind kind : ExpKind.values()){
            if(kind.kind == k){
                return kind;
            }
        }
        return null;
    }
}
