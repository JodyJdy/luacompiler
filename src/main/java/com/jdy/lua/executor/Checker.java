package com.jdy.lua.executor;

import com.jdy.lua.data.*;
import com.jdy.lua.statement.Expr;

public class Checker {

    public static Table checkTable(Value value) {
        if (!(value instanceof Table)) {
        throw new RuntimeException(value.toString() + " 不是表");
        }
        return (Table) value;
    }



    public static LuaFunction checkFunc(Value val){
        if (!(val instanceof LuaFunction)) {
            throw new RuntimeException("函数不存在");
        }
        return (LuaFunction) val;
    }

    public static Expr.NameExpr checkName(Expr expr) {
        if (!(expr instanceof Expr.NameExpr)) {
            throw new RuntimeException(expr.toString()+ " 不是NameExpr");
        }
        return (Expr.NameExpr) expr;
    }

    public static NumberValue checkNumber(Expr expr) {
        if (!(expr instanceof NumberValue)) {
            throw new RuntimeException(expr.toString()+ " 不是数字");
        }
        return (NumberValue) expr;
    }

    public static StringValue checkString(Value value) {
        if (!(value instanceof StringValue)) {
            throw new RuntimeException(value.toString()+ " 不是字符串");
        }
        return (StringValue) value;
    }

    public static String checkStringVal(Value value) {
        if (!(value instanceof StringValue)) {
            throw new RuntimeException(value.toString()+ " 不是字符串");
        }
        return  ((StringValue) value).getVal();
    }


    public static CalculateValue checkCalculate(Value value) {
        if (!(value instanceof CalculateValue)) {
            throw new RuntimeException(value.toString()+ " 不是数字");
        }
        return (CalculateValue) value;
    }

    public static NumberValue checkNumber(Value value) {
        if (!(value instanceof NumberValue)) {
            throw new RuntimeException(value.toString()+ " 不是数字");
        }
        return (NumberValue) value;
    }

    public static void checkNull(Object obj, String msg) {
        if (obj == null || obj == NilValue.NIL) {
            throw new RuntimeException(msg);
        }
    }

    public static BoolValue checkBool(Object obj) {
        if (obj instanceof BoolValue b) {
            return b;
        }
        throw new RuntimeException("值类型不是布尔值");
    }

    public static MultiValue checkMultiValue(Value val) {
        if (val instanceof MultiValue mul) {
            return mul;
        }
        throw new RuntimeException("值类型不是多值类型");
    }
}
