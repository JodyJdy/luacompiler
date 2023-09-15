package com.jdy.lua.executor;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jdy
 * @title: Table
 * @description:
 * @data 2023/9/14 16:33
 */
public class Table implements Value {
    public final Map<Value, Value> map = new LinkedHashMap<>();
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.TABLE;
    }

    public void add(Value key, Value value) {
        map.put(key, value);
    }
    public void add(Value value){
        map.put(new NumberValue(map.size()),value);
    }
}
