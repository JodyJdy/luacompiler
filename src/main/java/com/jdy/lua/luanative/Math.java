package com.jdy.lua.luanative;

import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.Table;

import static com.jdy.lua.executor.Checker.checkNumber;

/**
 * 数学相关函数
 */
public class Math {
    /**
     * table 名为 math
     */
    public static String TABLE_NAME = "math";
    public static Table MATH  = new Table();
    static {
        // math.add(a,b)
        MATH.addVal("add",add());
        // math.pi()
        MATH.addVal("pi", pi());
    }

    private static NativeFunction add(){
        return NativeFunction.builder().funcName("setmetatable")
                .parameterNames("a","b")
                .execute(
                        arg->{
                            NumberValue numberA = checkNumber(arg.get(0));
                            NumberValue numberB = checkNumber(arg.get(1));
                            return new NumberValue(numberA.getF() + numberB.getF());
                        }
                ).build();

    }
    private static NativeFunction pi(){
        return NativeFunction.builder().funcName("pi")
                .execute(
                        arg-> new NumberValue((float) java.lang.Math.PI)
                ).build();
    }
}
