package com.jdy.lua.data;

import lombok.Getter;

/**
 * @author jdy
 * @title: DataTypeEnum
 * @description:
 * @data 2023/9/14 16:24
 */
@Getter
public enum DataTypeEnum {

    NIL("nil"),
    BOOLEAN("boolean"),
    NUMBER("number"),
    STRING("string"),
    FUNCTION("function"),
    TABLE("table"),
    MULTI("multi"),
   // 暂时不支持 thread和userdata
    VM_RUNTIME_FUNC("vm_runtime_func")
    ;
    private final String str;
    DataTypeEnum(String str) {
        this.str = str;
    }

}
