package com.jdy.lua.data;

import java.util.Set;

/**
 * 元表的信息
 */
public class MetaTable {
    /**
     * 支持的元表类型
     */
    public static StringValue ADD = new StringValue("__add");
    public static StringValue SUB = new StringValue("__sub");
    public static StringValue MUL = new StringValue("__mul");
    public static StringValue DIV = new StringValue("__div");
    public static StringValue MOD = new StringValue("__mod");
    public static StringValue UNM = new StringValue("__unm");
    public static StringValue POW = new StringValue("__pow");
    public static StringValue CONCAT = new StringValue("__concat");

    public static StringValue NE = new StringValue("__ne");
    public static StringValue EQ = new StringValue("__eq");

    public static StringValue LT = new StringValue("__lt");
    public static StringValue LE = new StringValue("__le");
    public static StringValue INDEX = new StringValue("__index");
    public static StringValue NEW_INDEX = new StringValue("__newindex");

    public static StringValue TO_STRING = new StringValue("__tostring");

    public static Set<StringValue> META_SET = Set.of(
            ADD, SUB, MUL, DIV, MOD, UNM, POW, CONCAT,
            EQ, LT, LE, NE, INDEX, NEW_INDEX, TO_STRING
    );


}
