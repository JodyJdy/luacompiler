package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jdy.lua.data.MetaTable.INDEX;
import static com.jdy.lua.data.MetaTable.TO_STRING;

/**
 * @author jdy
 * @title: Table
 * @description:
 * @data 2023/9/14 16:33
 */
public class Table implements Value {


    public final Map<String, Value> map = new LinkedHashMap<>();

    @Override
    public String toString() {
        if (map.containsKey(TO_STRING)) {
            Value val = map.get(TO_STRING);
            if (val instanceof Function) {
                Executor executor = new Executor((Function) val, new ArrayList<>());
                return executor.execute().toString();
            }
        }
        return String.valueOf(map);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.TABLE;
    }

    public Table getMetatable() {
        return (Table) map.get(INDEX);
    }


    public void add(Value key, Value value) {
        if (key instanceof NumberValue) {
            add(String.valueOf(((NumberValue) key).getF()), value);
        } else if (key instanceof StringValue) {
            add(((StringValue) key).getVal(), value);
        } else {
            throw new RuntimeException("不支持的表索引类型");
        }
    }

    public void add(String key, Value value) {
        map.put(key, value);
    }

    public void add(Value value) {
        map.put(String.valueOf(map.size()), value);
    }

    public Value get(float key) {
        // lua 下标从1开始
        return get(String.valueOf(key-1));
    }

    public Value get(int key) {
        // lua 下标从1开始
        return get(String.valueOf(key-1));
    }

    public Value get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        //从元表获取数据
        if (map.containsKey(INDEX)) {
            return getFromMetaTable(map.get(INDEX),key);
        }
        return NilValue.NIL;
    }

    private Value getFromMetaTable(Value meta,String key) {
        //从元表中返回数据
        if (meta instanceof Table) {
            return ((Table) meta).get(key);
        } else if (meta instanceof Function) {
            Function call = (Function) meta;
            Expr.Function body = call.getBody();
            //只支持无参函数调用， 有参的没有使用场景
            if (body.getParamNames().isEmpty()) {
                Executor executor = new Executor(call, new ArrayList<>());
                return executor.execute();
            }
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
