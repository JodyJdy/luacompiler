package com.jdy.lua.data;

/**
 * @author jdy
 * @title: Value
 * @description:
 * @data 2023/9/14 16:26
 */
public interface Value {
    DataTypeEnum type();
    BoolValue eq(Value b);
    BoolValue ne(Value b);
    BoolValue lt(Value b);
    BoolValue gt(Value b);

    BoolValue le(Value b);
    BoolValue ge(Value b);

}
