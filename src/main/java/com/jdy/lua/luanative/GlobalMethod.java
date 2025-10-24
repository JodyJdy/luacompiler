package com.jdy.lua.luanative;

import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.StringValue;
import com.jdy.lua.data.Table;
import com.jdy.lua.data.Value;

import static com.jdy.lua.executor.Checker.checkTable;

public class GlobalMethod {

    public static NativeJavaFunction type(){
        return NativeJavaFunction.builder().funcName("type")
                .parameterNames("a")
                .execute(
                        arg-> new StringValue(arg.get(0).type().getStr())
                ).build();
    }


    public static NativeJavaFunction print(){
        return NativeJavaFunction.builder().funcName("print")
                .hasMultiVar()
                .execute(
                        arg->{
                            final StringBuilder sb = new StringBuilder();
                            arg.forEach(g->{
                                sb.append(g);
                                sb.append(" ");
                            });
                            System.out.println(sb);
                            return NilValue.NIL;
                        }
                ).build();
    }

    public static NativeJavaFunction println(){
        NativeJavaFunction.Builder builder = NativeJavaFunction.builder();
        builder.funcName("println");
        builder.hasMultiVar();
        builder.execute(
                arg -> {
                    final StringBuilder sb = new StringBuilder();
                    arg.forEach(sb::append);
                    System.out.println(sb);
                    return NilValue.NIL;
                }
        );
        return builder.build();
    }


    /**
     * setmetatable 函数
     */
    public static NativeJavaFunction setmetatable(){
        return NativeJavaFunction.builder().funcName("setmetatable")
                .parameterNames("a","b")
                .execute(
                        arg->{
                            Table tableA = checkTable(arg.get(0));
                            Table tableB = checkTable(arg.get(1));
                            tableA.setMetatable(tableB);
                            return tableA;
                        }
                ).build();
    }

    /**
     * getmetatable
     */
    public static NativeJavaFunction getmetatable(){
        return NativeJavaFunction.builder().funcName("getmetatable")
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
