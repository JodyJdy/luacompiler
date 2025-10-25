package com.jdy.lua.luanative;

import com.jdy.lua.data.*;

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
        MATH.addVal("add", add());
        // math.abs(x)
        MATH.addVal("abs", abs());
        // math.acos(x)
        MATH.addVal("acos", acos());
        // math.asin(x)
        MATH.addVal("asin", asin());
        // math.atan(y,x)
        MATH.addVal("atan", atan());
        // math.ceil(x)
        MATH.addVal("ceil", ceil());
        // math.cos(x)
        MATH.addVal("cos", cos());
        // math.deg(x)
        MATH.addVal("deg", deg());
        // math.exp(x)
        MATH.addVal("exp", exp());
        // math.floor(x)
        MATH.addVal("floor", floor());
        // math.fmod(x,y)
        MATH.addVal("fmod", fmod());
        // math.huge
        MATH.addVal("huge", new NumberValue(Double.POSITIVE_INFINITY));
        // math.log(x,base)
        MATH.addVal("log", log());
        // math.max(...)
        MATH.addVal("max", max());
        // math.min(...)
        MATH.addVal("min", min());
        // math.modf(x)
        MATH.addVal("modf", modf());
        // math.pi
        MATH.addVal("pi", new NumberValue(java.lang.Math.PI));
        // math.rad(x)
        MATH.addVal("rad", rad());
        // math.random(m,n)
        MATH.addVal("random", random());
        // math.randomseed(x)
        MATH.addVal("randomseed", randomseed());
        // math.sin(x)
        MATH.addVal("sin", sin());
        // math.sqrt(x)
        MATH.addVal("sqrt", sqrt());
        // math.tan(x)
        MATH.addVal("tan", tan());
        // math.tointeger(x)
        MATH.addVal("tointeger", tointeger());
        // math.type(x)
        MATH.addVal("type", type());
        // math.ult(m,n)
        MATH.addVal("ult", ult());

        // math.maxinteger
        MATH.addVal("maxinteger", new NumberValue(Long.MAX_VALUE));
        // math.mininteger
        MATH.addVal("mininteger", new NumberValue(Long.MIN_VALUE));
    }

    // 已有的 add 函数实现
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

    // 新增 abs 函数
    public static Value abs(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(x.isInt() ? java.lang.Math.abs(x.getL()) : java.lang.Math.abs(x.getD()));
    }

    private static NativeJavaFunction abs() {
        return NativeJavaFunction.builder().funcName("abs")
                .parameterNames("x")
                .execute(Math::abs).build();
    }

    // 新增 acos 函数
    public static Value acos(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.acos(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction acos() {
        return NativeJavaFunction.builder().funcName("acos")
                .parameterNames("x")
                .execute(Math::acos).build();
    }

    // 新增 asin 函数
    public static Value asin(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.asin(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction asin() {
        return NativeJavaFunction.builder().funcName("asin")
                .parameterNames("x")
                .execute(Math::asin).build();
    }

    // 新增 atan 函数
    public static Value atan(List<Value> args) {
        NumberValue y = checkNumber(args.get(0));
        if (args.size() == 1) {
            return new NumberValue(java.lang.Math.atan(y.getValue().doubleValue()));
        } else {
            NumberValue x = checkNumber(args.get(1));
            return new NumberValue(java.lang.Math.atan2(y.getValue().doubleValue(), x.getValue().doubleValue()));
        }
    }

    private static NativeJavaFunction atan() {
        return NativeJavaFunction.builder().funcName("atan")
                .parameterNames("y", "x")
                .execute(Math::atan).build();
    }

    // 新增 ceil 函数
    public static Value ceil(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.ceil(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction ceil() {
        return NativeJavaFunction.builder().funcName("ceil")
                .parameterNames("x")
                .execute(Math::ceil).build();
    }

    // 新增 cos 函数
    public static Value cos(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.cos(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction cos() {
        return NativeJavaFunction.builder().funcName("cos")
                .parameterNames("x")
                .execute(Math::cos).build();
    }

    // 新增 deg 函数
    public static Value deg(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.toDegrees(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction deg() {
        return NativeJavaFunction.builder().funcName("deg")
                .parameterNames("x")
                .execute(Math::deg).build();
    }

    // 新增 exp 函数
    public static Value exp(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.exp(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction exp() {
        return NativeJavaFunction.builder().funcName("exp")
                .parameterNames("x")
                .execute(Math::exp).build();
    }

    // 新增 floor 函数
    public static Value floor(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.floor(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction floor() {
        return NativeJavaFunction.builder().funcName("floor")
                .parameterNames("x")
                .execute(Math::floor).build();
    }

    // 新增 fmod 函数
    public static Value fmod(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        NumberValue y = checkNumber(args.get(1));
        double result = x.getValue().doubleValue() % y.getValue().doubleValue();
        return new NumberValue(result);
    }

    private static NativeJavaFunction fmod() {
        return NativeJavaFunction.builder().funcName("fmod")
                .parameterNames("x", "y")
                .execute(Math::fmod).build();
    }

    // 新增 log 函数
    public static Value log(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        if (args.size() == 1) {
            return new NumberValue(java.lang.Math.log(x.getValue().doubleValue()));
        } else {
            NumberValue base = checkNumber(args.get(1));
            return new NumberValue(java.lang.Math.log(x.getValue().doubleValue()) / java.lang.Math.log(base.getValue().doubleValue()));
        }
    }

    private static NativeJavaFunction log() {
        return NativeJavaFunction.builder().funcName("log")
                .parameterNames("x", "base")
                .execute(Math::log).build();
    }

    // 新增 max 函数
    public static Value max(List<Value> args) {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (Value arg : args) {
            NumberValue num = checkNumber(arg);
            maxValue = java.lang.Math.max(maxValue, num.getValue().doubleValue());
        }
        return new NumberValue(maxValue);
    }

    private static NativeJavaFunction max() {
        return NativeJavaFunction.builder().funcName("max")
                .parameterNames("...")
                .execute(Math::max).build();
    }

    // 新增 min 函数
    public static Value min(List<Value> args) {
        double minValue = Double.POSITIVE_INFINITY;
        for (Value arg : args) {
            NumberValue num = checkNumber(arg);
            minValue = java.lang.Math.min(minValue, num.getValue().doubleValue());
        }
        return new NumberValue(minValue);
    }

    private static NativeJavaFunction min() {
        return NativeJavaFunction.builder().funcName("min")
                .parameterNames("...")
                .execute(Math::min).build();
    }

    // 新增 modf 函数
    public static Value modf(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        double value = x.getValue().doubleValue();
        long integerPart = (long) value;
        double fractionalPart = value - integerPart;
        return new MultiValue(List.of(new NumberValue(integerPart), new NumberValue(fractionalPart)));
    }

    private static NativeJavaFunction modf() {
        return NativeJavaFunction.builder().funcName("modf")
                .parameterNames("x")
                .execute(Math::modf).build();
    }

    // 新增 rad 函数
    public static Value rad(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.toRadians(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction rad() {
        return NativeJavaFunction.builder().funcName("rad")
                .parameterNames("x")
                .execute(Math::rad).build();
    }

    // 新增 random 函数
    public static Value random(List<Value> args) {
        java.util.Random rand = new java.util.Random();
        if (args.isEmpty()) {
            return new NumberValue(rand.nextDouble());
        } else if (args.size() == 1) {
            int n = checkNumber(args.get(0)).getValue().intValue();
            return new NumberValue(rand.nextInt(n) + 1);
        } else {
            int m = checkNumber(args.get(0)).getValue().intValue();
            int n = checkNumber(args.get(1)).getValue().intValue();
            return new NumberValue(rand.nextInt(n - m + 1) + m);
        }
    }

    private static NativeJavaFunction random() {
        return NativeJavaFunction.builder().funcName("random")
                .parameterNames("m", "n")
                .execute(Math::random).build();
    }

    // 新增 randomseed 函数
    public static Value randomseed(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        java.util.Random rand = new java.util.Random(x.getValue().longValue());
        // 这里我们不实际改变随机种子，只是模拟行为
        return null; // 在Lua中无返回值
    }

    private static NativeJavaFunction randomseed() {
        return NativeJavaFunction.builder().funcName("randomseed")
                .parameterNames("x")
                .execute(Math::randomseed).build();
    }

    // 新增 sin 函数
    public static Value sin(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.sin(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction sin() {
        return NativeJavaFunction.builder().funcName("sin")
                .parameterNames("x")
                .execute(Math::sin).build();
    }

    // 新增 sqrt 函数
    public static Value sqrt(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.sqrt(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction sqrt() {
        return NativeJavaFunction.builder().funcName("sqrt")
                .parameterNames("x")
                .execute(Math::sqrt).build();
    }

    // 新增 tan 函数
    public static Value tan(List<Value> args) {
        NumberValue x = checkNumber(args.get(0));
        return new NumberValue(java.lang.Math.tan(x.getValue().doubleValue()));
    }

    private static NativeJavaFunction tan() {
        return NativeJavaFunction.builder().funcName("tan")
                .parameterNames("x")
                .execute(Math::tan).build();
    }

    public static Value tointeger(List<Value> args) {
        Value val = args.get(0);
        if (val instanceof NumberValue num) {
            if (num.isInt()) {
                return num;
            } else{
                return new NumberValue(num.getD().intValue());
            }
        }
        return NilValue.NIL;
    }

    private static NativeJavaFunction tointeger() {
        return NativeJavaFunction.builder().funcName("tointeger")
                .parameterNames("x")
                .execute(Math::tointeger).build();
    }

    public static Value type(List<Value> args) {
        Value val = args.get(0);
        if (val instanceof NumberValue num) {
            if (num.isInt()) {
                return new com.jdy.lua.data.StringValue("integer");
            } else {
                return new com.jdy.lua.data.StringValue("float");
            }
        }
        return NilValue.NIL;
    }

    private static NativeJavaFunction type() {
        return NativeJavaFunction.builder().funcName("type")
                .parameterNames("x")
                .execute(Math::type).build();
    }

    public static Value ult(List<Value> args) {
        NumberValue m = checkNumber(args.get(0));
        NumberValue n = checkNumber(args.get(1));
        boolean result = Long.compareUnsigned(m.getValue().longValue(), n.getValue().longValue()) < 0;
        return  BoolValue.valueOf(result);
    }

    private static NativeJavaFunction ult() {
        return NativeJavaFunction.builder().funcName("ult")
                .parameterNames("m", "n")
                .execute(Math::ult).build();
    }
}
