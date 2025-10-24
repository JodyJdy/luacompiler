package com.jdy.lua.luanative;

import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.Table;
import com.jdy.lua.data.Value;

import java.util.List;

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
        // math.pi
        MATH.addVal("pi", new NumberValue(java.lang.Math.PI));
    }

    public static Value add(List<Value> args) {
        NumberValue numberA = checkNumber(args.get(0));
        NumberValue numberB = checkNumber(args.get(1));
        return numberA.add(numberB);
    }
    private static NativeJavaFunction add(){
        return NativeJavaFunction.builder().funcName("add")
                .parameterNames("a","b")
                .execute(Math::add).build();

    }
}
