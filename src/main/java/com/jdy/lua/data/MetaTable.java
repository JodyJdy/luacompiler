package com.jdy.lua.data;

import java.util.Set;

/**
 * 元表的信息
 */
public class MetaTable {
    /**
     * 支持的元表类型
     */
    public static String ADD = "__add";
    public static String SUB = "__sub";
    public static String MUL = "__mul";
    public static String DIV = "__div";
    public static String MOD = "__mod";
    public static String UNM = "__unm";
    public static String CONCAT = "__concat";

    public static String EQ = "__eq";

    public static String LT = "__lt";
    public static String LE = "__le";
    public static String INDEX = "__index";
    public static String NEW_INDEX = "__newindex";

    public static String TO_STRING = "__tostring";

    public static Set<String> META_SET = Set.of(
            ADD,SUB,MUL,DIV,MOD,UNM,CONCAT,EQ,LT,LE,INDEX,NEW_INDEX,TO_STRING
    );
}
