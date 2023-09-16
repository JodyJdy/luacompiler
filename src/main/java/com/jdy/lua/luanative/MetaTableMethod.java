package com.jdy.lua.luanative;

import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Table;
import com.jdy.lua.data.Value;

import static com.jdy.lua.data.MetaTable.META_SET;
import static com.jdy.lua.executor.Checker.checkTable;

/**
 * 元表相关的方法
 */
public class MetaTableMethod {
    /**
     * setmetatable 函数
     */
    public static NativeFunction setmetatable(){
        return NativeFunction.builder().funcName("setmetatable")
                .parameterNames("a","b")
                .execute(
                        arg->{
                            Table tableA = checkTable(arg.get(0));
                            Table tableB = checkTable(arg.get(1));
                            for (String meta : META_SET) {
                                Value v = tableB.get(meta);
                                if (v != NilValue.NIL) {
                                    tableA.add(meta,v);
                                }
                            }
                            return tableA;
                        }
                ).build();
    }

    /**
     * getmetatable
     */
    public static NativeFunction getmetatable(){
        return NativeFunction.builder().funcName("getmetatable")
                .parameterNames("a")
                .execute(
                        arg->{
                            Value a = arg.get(0);
                            Table tableA = checkTable(a);
                            return tableA.getMetatable();
                        }
                ).build();

    }

}
