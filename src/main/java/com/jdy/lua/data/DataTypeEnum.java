package com.jdy.lua.data;

/**
 * @author jdy
 * @title: DataTypeEnum
 * @description:
 * @data 2023/9/14 16:24
 */
public enum DataTypeEnum {
    NIL,
    BOOLEAN,
    NUMBER,
    STRING,
    FUNCTION,
    TABLE,
   // 暂时不支持 thread和userdata
    ;
}
