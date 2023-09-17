package com.jdy.lua.data;

/**
 * @author jdy
 * @title: DataTypeEnum
 * @description:
 * @data 2023/9/14 16:24
 */
public enum DataTypeEnum {

    NIL("nil"),
    BOOLEAN("boolean"),
    NUMBER("number"),
    STRING("string"),
    FUNCTION("function"),
    TABLE("table"),
    MULTI("multi"),
   // 暂时不支持 thread和userdata
    ;
    private final String str;
    DataTypeEnum(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
