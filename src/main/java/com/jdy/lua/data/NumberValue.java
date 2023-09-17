package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import lombok.Data;

import java.util.Objects;

/**
 * @author jdy
 * @title: NumberValue
 * @description:
 * @data 2023/9/14 16:29
 */
@Data
public class NumberValue implements CalculateValue, Expr {

    private Float f;

    public NumberValue(Float f) {
        this.f = f;
    }

    public NumberValue(int i) {
        this.f = (float) i;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return new NumberValue(f);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.NUMBER;
    }

    @Override
    public String toString() {
        return String.valueOf(f);
    }

    @Override
    public BoolValue eq(Value b) {
        if (b instanceof NumberValue num) {
            return Objects.equals(this.f, num.f) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        if (b instanceof NumberValue num) {
            return !Objects.equals(this.f, num.f) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        if (b instanceof NumberValue num) {
            return !Objects.equals(this.f, num.f) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        if (b instanceof NumberValue num) {
            return this.f > num.getF() ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        if (b instanceof NumberValue num) {
            return this.f <= num.getF() ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        if (b instanceof NumberValue num) {
            return this.f >= num.getF() ? BoolValue.TRUE : BoolValue.FALSE;
        }
        throw new RuntimeException("不同类型无法比较大小");
    }


    @Override
    public Value add(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f + num.f);
        }
        return CalculateValue.super.add(b);
    }

    @Override
    public Value sub(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f - num.f);
        }
        return CalculateValue.super.sub(b);
    }

    @Override
    public Value mul(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f * num.f);
        }
        return CalculateValue.super.mul(b);
    }

    @Override
    public Value div(Value b) {
        if (b instanceof NumberValue num) {
            if (num.f == 0) {
                throw new RuntimeException("除数不能为0");
            }
            return new NumberValue(this.f / num.f);
        }
        return CalculateValue.super.div(b);
    }

    @Override
    public Value unm() {
        return new NumberValue(~(this.f.intValue()));
    }

    @Override
    public Value mod(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f % num.f);
        }
        return CalculateValue.super.mod(b);
    }

    @Override
    public Value intMod(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f.intValue() % num.f.intValue());
        }
        return CalculateValue.super.intMod(b);
    }

    @Override
    public Value pow(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue((float) Math.pow(this.f , num.f));
        }
        return CalculateValue.super.pow(b);
    }

    @Override
    public Value bitAnd(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f.intValue() & num.f.intValue());
        }
        return CalculateValue.super.bitAnd(b);
    }

    @Override
    public Value bitOr(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f.intValue() | num.f.intValue());
        }
        return CalculateValue.super.bitOr(b);
    }

    @Override
    public Value bitLeftMove(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f.intValue() << num.f.intValue());
        }
        return CalculateValue.super.bitLeftMove(b);
    }

    @Override
    public Value bitRightMove(Value b) {
        if (b instanceof NumberValue num) {
            return new NumberValue(this.f.intValue() >> num.f.intValue());
        }
        return CalculateValue.super.bitRightMove(b);
    }
}
