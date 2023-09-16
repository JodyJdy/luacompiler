package com.jdy.lua.data;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jdy
 * @title: Table
 * @description:
 * @data 2023/9/14 16:33
 */
public class Table implements Value {
    public final Map<String, Value> map = new LinkedHashMap<>();
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.TABLE;
    }

    public void add(Value key, Value value) {
        if (value instanceof NumberValue) {
            add(String.valueOf(((NumberValue) value).getF()),value);
        }
        if (value instanceof StringValue) {
            add(((StringValue) value).getVal(), value);
        }
        throw new RuntimeException("不支持的表索引类型");

    }
    public void add(String key, Value value) {
        map.put(key, value);
    }
    public void add(Value value){
        map.put(String.valueOf(map.size()),value);
    }

    public Value get(float key) {
        return get(String.valueOf(key));
    }
    public Value get(int key) {
        return get(String.valueOf(key));
    }
    public Value get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return NilValue.NIL;
    }

    public Value get(Value value) {
        if (value instanceof NumberValue) {
            return get(((NumberValue) value).getF());
        }
        if (value instanceof StringValue) {
            return get(((StringValue) value).getVal());
        }
        throw new RuntimeException("不支持索引的类型");
    }
}
