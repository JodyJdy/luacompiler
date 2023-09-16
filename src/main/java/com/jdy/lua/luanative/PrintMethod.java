package com.jdy.lua.luanative;

import com.jdy.lua.data.NilValue;

public class PrintMethod {

    public static NativeFunction print(){
        return NativeFunction.builder().funcName("print")
                .hasMultiVar()
                .execute(
                        arg->{
                            final StringBuilder sb = new StringBuilder();
                            arg.forEach(sb::append);
                            System.out.println(sb);
                            return NilValue.NIL;
                        }
                ).build();
    }

    public static NativeFunction println(){
        NativeFunction.Builder builder = NativeFunction.builder();
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


}
