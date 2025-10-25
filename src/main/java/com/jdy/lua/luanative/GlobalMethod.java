package com.jdy.lua.luanative;

import com.jdy.lua.data.*;

import java.util.ArrayList;
import java.util.List;

import static com.jdy.lua.executor.Checker.checkTable;

public class GlobalMethod {

    /**
     * assert 函数实现
     */
    public static Value assertFunc(List<Value> args) {
        Value v = args.get(0);
        // 如果第一个参数为假(nil或false)，则抛出错误
        if (NilValue.NIL.equals(v)|| BoolValue.FALSE.equals(v)) {
            String message = "assertion failed!";
            if (args.size() > 1 && args.get(1) != NilValue.NIL) {
                message = args.get(1).toString();
            }
            throw new RuntimeException(message);
        }
        // 否则返回所有参数
        if (args.size() == 1) {
            return v;
        } else {
            return new MultiValue(args);
        }
    }

    public static NativeJavaFunction assertFunc() {
        return NativeJavaFunction.builder().funcName("assert")
                .parameterNames("v", "message")
                .execute(GlobalMethod::assertFunc).build();
    }

    /**
     * collectgarbage 函数实现（简化版）
     */
    public static Value collectgarbage(List<Value> args) {
        // 简化实现，仅支持"collect"和默认操作
        if (args.isEmpty() || "collect".equals(args.get(0).toString())) {
            System.gc(); // 触发垃圾回收
            // 返回近似的内存使用量(KB)
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            return new NumberValue(usedMemory / 1024.0);
        } else if ("isrunning".equals(args.get(0).toString())) {
            // 简化实现，始终返回true
            return BoolValue.TRUE;
        }
        // 其他选项简化处理
        return NilValue.NIL;
    }

    public static NativeJavaFunction collectgarbage() {
        return NativeJavaFunction.builder().funcName("collectgarbage")
                .parameterNames("opt", "arg")
                .execute(GlobalMethod::collectgarbage).build();
    }

    /**
     * error 函数实现
     */
    public static Value error(List<Value> args) {
        String message = "error";
        if (!args.isEmpty() && args.get(0) != NilValue.NIL) {
            message = args.get(0).toString();
        }
        // 抛出运行时异常模拟Lua的error函数
        throw new RuntimeException(message);
    }

    public static NativeJavaFunction error() {
        return NativeJavaFunction.builder().funcName("error")
                .parameterNames("message", "level")
                .execute(GlobalMethod::error).build();
    }

    /**
     * pcall 函数实现
     */
    public static Value pcall(List<Value> args) {
        if (args.isEmpty()) {
            return MultiValue.of(BoolValue.FALSE, new StringValue("bad argument #1 to 'pcall' (value expected)"));
        }

        try {
            Value func = args.get(0);
            if (func instanceof NativeJavaFunction) {
                List<Value> funcArgs = args.subList(1, args.size());
                Value result = ((NativeJavaFunction) func).execute(funcArgs);

                List<Value> successResult = new ArrayList<>();
                successResult.add(BoolValue.TRUE);
                if (result != null) {
                    successResult.add(result);
                }
                return MultiValue.of(successResult);
            } else {
                return MultiValue.of(BoolValue.FALSE, new StringValue("attempt to call a " + func.type().getStr() + " value"));
            }
        } catch (Exception e) {
            return MultiValue.of(BoolValue.FALSE, new StringValue(e.getMessage()));
        }
    }

    public static NativeJavaFunction pcall() {
        return NativeJavaFunction.builder().funcName("pcall")
                .parameterNames("f", "arg1", "...")
                .execute(GlobalMethod::pcall).build();
    }

    /**
     * rawequal 函数实现
     */
    public static Value rawequal(List<Value> args) {
        Value v1 = args.get(0);
        Value v2 = args.get(1);
        return BoolValue.valueOf(v1.equals(v2));
    }

    public static NativeJavaFunction rawequal() {
        return NativeJavaFunction.builder().funcName("rawequal")
                .parameterNames("v1", "v2")
                .execute(GlobalMethod::rawequal).build();
    }

    /**
     * rawget 函数实现
     */
    public static Value rawget(List<Value> args) {
        Table table = checkTable(args.get(0));
        Value index = args.get(1);
        return table.get(index, false);
    }

    public static NativeJavaFunction rawget() {
        return NativeJavaFunction.builder().funcName("rawget")
                .parameterNames("table", "index")
                .execute(GlobalMethod::rawget).build();
    }

    /**
     * rawlen 函数实现
     */
    public static Value rawlen(List<Value> args) {
        Value v = args.get(0);
        if (v instanceof Table table) {
            return table.len();
        } else if (v instanceof StringValue stringValue) {
            return stringValue.len();
        }
        return new NumberValue(0);
    }

    public static NativeJavaFunction rawlen() {
        return NativeJavaFunction.builder().funcName("rawlen")
                .parameterNames("v")
                .execute(GlobalMethod::rawlen).build();
    }

    /**
     * rawset 函数实现
     */
    public static Value rawset(List<Value> args) {
        Table table = checkTable(args.get(0));
        Value index = args.get(1);
        Value value = args.get(2);
        table.addVal(index, value, false);
        return table;
    }

    public static NativeJavaFunction rawset() {
        return NativeJavaFunction.builder().funcName("rawset")
                .parameterNames("table", "index", "value")
                .execute(GlobalMethod::rawset).build();
    }
    public static Value type(List<Value> arg) {
       return new StringValue(arg.get(0).type().getStr());
    }
    public static NativeJavaFunction type(){
        return NativeJavaFunction.builder().funcName("type")
                .parameterNames("a")
                .execute(GlobalMethod::type).build();
    }

    public static Value print(List<Value> arg) {
        final StringBuilder sb = new StringBuilder();
        arg.forEach(g->{
            sb.append(g);
            sb.append(" ");
        });
        System.out.println(sb);
        return NilValue.NIL;
    }

    public static NativeJavaFunction print(){
        return NativeJavaFunction.builder().funcName("print")
                .hasMultiVar()
                .execute(GlobalMethod::print).build();
    }

    public static Value println(List<Value> arg) {
        final StringBuilder sb = new StringBuilder();
        arg.forEach(sb::append);
        System.out.println(sb);
        return NilValue.NIL;
    }
    public static NativeJavaFunction println(){
        NativeJavaFunction.Builder builder = NativeJavaFunction.builder();
        builder.funcName("println");
        builder.hasMultiVar();
        builder.execute(GlobalMethod::println);
        return builder.build();
    }


    public static Value setmetatable(List<Value> arg) {
        Table tableA = checkTable(arg.get(0));
        Table tableB = checkTable(arg.get(1));
        tableA.setMetatable(tableB);
        return tableA;
    }
    /**
     * setmetatable 函数
     */
    public static NativeJavaFunction setmetatable(){
        return NativeJavaFunction.builder().funcName("setmetatable")
                .parameterNames("a","b")
                .execute(GlobalMethod::setmetatable).build();
    }

    public static Value getmetatable(List<Value> arg) {
        Value a = arg.get(0);
        Table tableA = checkTable(a);
        return tableA.getMetatable();
    }

    /**
     * getmetatable
     */
    public static NativeJavaFunction getmetatable(){
        return NativeJavaFunction.builder().funcName("getmetatable")
                .parameterNames("a")
                .execute(GlobalMethod::getmetatable).build();

    }

    /**
     * next 函数实现
     */
    public static Value next(List<Value> args) {
        if(args.size() == 0 || NilValue.NIL.equals(args.get(0))){
            return NilValue.NIL;
        }
        Table table = checkTable(args.get(0));
        Value index = args.size() > 1 ? args.get(1) : NilValue.NIL;
        int from;
        if (NilValue.NIL.equals(index)) {
            //由于需要访问下一个索引，所以从-1开始
            from = -1;
        }else if (index instanceof NumberValue numberValue) {
            from = table.keys().indexOf(numberValue.toString());
        } else if (index instanceof StringValue stringValues) {
            from = table.keys().indexOf(stringValues.getVal());
        } else{
            return NilValue.NIL;
        }
        //访问下一个索引
        from++;
        while (from < table.keys().size()) {
            if (table.hasKey(table.key(from)) && !table.get(table.key(from)).equals(NilValue.NIL)) {
                return  MultiValue.of(new StringValue(table.key(from)), table.get(table.key(from)));
            }
        }
        // 更复杂的索引逻辑在此简化处理
        return NilValue.NIL;
    }

    public static NativeJavaFunction next() {
        return NativeJavaFunction.builder().funcName("next")
                .parameterNames("table", "index")
                .execute(GlobalMethod::next).build();
    }
}
