package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import lombok.Data;

/**
 * @author jdy
 * @title: StringValue
 * @description:
 * @data 2023/9/14 16:27
 */
@Data
public class StringValue implements CalculateValue, Expr {
    private String val;

    public StringValue() {
    }

    public StringValue(StringValue left, StringValue right) {
        this.val = left.getVal() + right.getVal();
    }

    public StringValue(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.STRING;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return new StringValue(val);
    }


    @Override
    public BoolValue eq(Value b) {
        if (b instanceof StringValue str) {
            return this.getVal().equals(str.getVal()) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        if (b instanceof StringValue str) {
            return !this.getVal().equals(str.getVal()) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        if (b instanceof StringValue str) {
            return this.getVal().compareTo(str.getVal()) < 0 ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        if (b instanceof StringValue str) {
            return this.getVal().compareTo(str.getVal()) > 0 ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        if (b instanceof StringValue str) {
            return this.getVal().compareTo(str.getVal()) <= 0 ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        if (b instanceof StringValue str) {
            return this.getVal().compareTo(str.getVal()) >= 0 ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public Value concat(Value b) {
        if (b instanceof StringValue str) {
            return new StringValue(this.val + str.val);
        }
        return CalculateValue.super.concat(b);
    }

    @Override
    public Value len() {
        return new NumberValue(val.length());
    }
}
