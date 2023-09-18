package com.jdy.lua.data;

/**
 * @author jdy
 * @title: Value
 * @description:
 * @data 2023/9/14 16:26
 */
public interface Value {
    DataTypeEnum type();
    default BoolValue eq(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue ne(Value b){

        throw new RuntimeException("不支持比较");
    }
    default BoolValue lt(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue gt(Value b){
        throw new RuntimeException("不支持比较");
    }

    default BoolValue le(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue ge(Value b){
        throw new RuntimeException("不支持比较");
    }

}
