package com.jdy.lua.luanative;

import com.jdy.lua.data.*;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.vm.ByteCode;

import java.util.List;

/**
 * 泛型for循环
 */
public class GenericFor {


    public static Value ipairs(List<Value> arg) {
       return new MultiValue(List.of(
               f, arg.get(0), NilValue.NIL
       ));
    }

    /**
     * ipairs
     * 使用下标访问，全量访问
     * 返回 f,s,var
     */
    public static NativeJavaFunction ipairs(){
        return NativeJavaFunction.builder().funcName("ipairs")
                .parameterNames("a")
                .execute(GenericFor::ipairs).build();
    }
    private static final NativeJavaFunction f = ipairsIterator();

    public static Value ipairsIterator(List<Value> arg) {
        Table table = Checker.checkTable(arg.get(0));
        Value val = arg.get(1);
        //第一次迭代
        if (val == NilValue.NIL) {
            return new MultiValue(List.of(new NumberValue(1), table.get(table.key(0))));
        } else {
            int index = Checker.checkNumber(val).intValue();
            //迭代结束
            if (index >= table.keys().size()) {
                return new MultiValue(List.of(NilValue.NIL, NilValue.NIL));
            } else {
                return new MultiValue(List.of(new NumberValue(index + 1), table.get(table.key(index))));
            }
        }
    }
    private static NativeJavaFunction ipairsIterator(){
        return NativeJavaFunction.builder().funcName("f")
                .parameterNames("t","var")
                .execute(GenericFor::ipairsIterator).build();
    }


    public static Value pairs(List<Value> arg) {
       return new MultiValue(List.of(
               f2, arg.get(0), NilValue.NIL
       ));
    }
    /**
     * 使用 key访问
     * 全量访问
     */
    public static NativeJavaFunction pairs(){
        return NativeJavaFunction.builder().funcName("pairs")
                .parameterNames("a")
                .execute(GenericFor::pairs).build();
    }

    private static final NativeJavaFunction f2 = pairsIterator();

    public static Value pairsIterator(List<Value> arg) {
        Table table = Checker.checkTable(arg.get(0));
        Value val = arg.get(1);
        List<String> keys = table.keys();
        //第一次迭代
        if (val == NilValue.NIL) {
            return new MultiValue(List.of(new StringValue(keys.get(0)),table.get(keys.get(0))));
        } else{
            int index = keys.indexOf(Checker.checkString(val).getVal());
            //迭代结束
            if (index+1 >= table.keys().size()) {
                return new MultiValue(List.of(NilValue.NIL, NilValue.NIL));
            } else{
                return new MultiValue(List.of(new StringValue(keys.get(index+1)),table.get(keys.get(index+1))));
            }
        }
    }

    private static NativeJavaFunction pairsIterator(){
        return NativeJavaFunction.builder().funcName("f")
                .parameterNames("t","var")
                .execute(GenericFor::pairsIterator).build();
    }

}
