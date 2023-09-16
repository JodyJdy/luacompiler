package com.jdy.lua.executor;

import com.jdy.lua.data.StringValue;
import com.jdy.lua.data.Table;
import com.jdy.lua.data.Value;
import com.jdy.lua.statement.Expr;

public class Checker {

    public static void checkTable(Value value) {
        if (!(value instanceof Table)) {
        throw new RuntimeException(value.toString() + " 不是表");
        }
    }

    public static void checkName(Expr expr) {
        if (!(expr instanceof Expr.NameExpr)) {
            throw new RuntimeException(expr.toString()+ " 不是NameExpr");
        }
    }

    public static void checkName(Value value) {
        if (!(value instanceof StringValue)) {
            throw new RuntimeException(value.toString() + "不是字符串");
        }
    }
}
