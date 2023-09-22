package com.jdy.lua.luanative;

import com.jdy.lua.data.*;
import com.jdy.lua.executor.Checker;

import java.util.List;

/**
 * 泛型for循环
 */
public class GenericFor {

    /**
     * ipairs
     * 使用下标访问，全量访问
     * 返回 f,s,var
     */
    public static NativeFunction ipairs(){
        return NativeFunction.builder().funcName("ipairs")
                .parameterNames("a")
                .execute(
                        arg-> new MultiValue(List.of(
                             f, arg.get(0), NilValue.NIL
                        ))
                ).build();
    }
    private static final NativeFunction f = ipairsIterator();
    private static NativeFunction ipairsIterator(){
        return NativeFunction.builder().funcName("f")
                .parameterNames("t","var")
                .execute(
                        arg->{
                            Table table = Checker.checkTable(arg.get(0));
                            Value val = arg.get(1);
                            //第一次迭代
                            if (val == NilValue.NIL) {
                                return new MultiValue(List.of(new NumberValue(1),table.get(table.key(0))));
                            } else{
                                int index = Checker.checkNumber(val).intValue();
                                //迭代结束
                                if (index >= table.keys().size()) {
                                    return new MultiValue(List.of(NilValue.NIL, NilValue.NIL));
                                } else{
                                    return new MultiValue(List.of(new NumberValue(index+1),table.get(table.key(index))));
                                }
                            }
                        }
                ).build();
    }


    /**
     * 使用 key访问
     * 全量访问
     */
    public static NativeFunction pairs(){
        return NativeFunction.builder().funcName("pairs")
                .parameterNames("a")
                .execute(
                        arg-> new MultiValue(List.of(
                                f2, arg.get(0), NilValue.NIL
                        ))
                ).build();
    }

    private static final NativeFunction f2 = pairsIterator();
    private static NativeFunction pairsIterator(){
        return NativeFunction.builder().funcName("f")
                .parameterNames("t","var")
                .execute(
                        arg->{
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
                ).build();
    }

}
