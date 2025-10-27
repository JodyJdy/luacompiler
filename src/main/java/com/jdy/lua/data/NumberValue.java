package com.jdy.lua.data;

import com.jdy.lua.executor.Executor;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.ExprTypeEnum;
import lombok.Data;
import lombok.Getter;

import javax.swing.plaf.PanelUI;

import static com.jdy.lua.data.BoolValue.*;

import java.util.Objects;

/**
 * @author jdy
 * @title: NumberValue
 * @description:
 * @data 2023/9/14 16:29
 */
@Data
public class NumberValue implements CalculateValue, Expr {

    @Getter
    Double d;
    @Getter
    Long l;
    /**
     *是否是整形
     */
    boolean isInt;

    public NumberValue(int i) {
        this.l = (long) i;
        isInt = true;
    }

    public NumberValue(NumberValue n) {
            isInt = n.isInt;
            this.l = n.l;
            this.d= n.d;
    }

    public NumberValue(Long l) {
        this.l = l;
        isInt = true;
    }
    public NumberValue(Double d) {
       this.d = d;
       isInt = false;
    }

    @Override
    public Value visitExpr(Executor vistor) {
        return this;
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.NUMBER;
    }

    @Override
    public ExprTypeEnum exprType() {
        return ExprTypeEnum.NumberValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberValue that = (NumberValue) o;
        return Objects.equals(d, that.d) && Objects.equals(l, that.l);
    }

    @Override
    public int hashCode() {
        return Objects.hash(d, l);
    }

    @Override
    public String toString() {
        if (d != null) {
            return String.valueOf(d);
        }
        return String.valueOf(l);
    }

    @Override
    public BoolValue eq(Value b) {
        if (b instanceof NumberValue num) {
            return Objects.equals(this,num) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        if (b instanceof NumberValue num) {
            return !Objects.equals(this,num) ? BoolValue.TRUE : BoolValue.FALSE;
        }
        return BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
               double left = this.isInt ? l: d;
               double right = num.isInt ? num.l : num.d;
               return left < right ? BoolValue.TRUE:BoolValue.FALSE;
            } else{
               return this.l < num.l ? TRUE : FALSE;
            }
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return left > right ? BoolValue.TRUE:BoolValue.FALSE;
            } else{
                return this.l > num.l ? TRUE : FALSE;
            }
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return left <= right ? BoolValue.TRUE:BoolValue.FALSE;
            } else{
                return this.l <= num.l ? TRUE : FALSE;
            }
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return left >= right ? BoolValue.TRUE:BoolValue.FALSE;
            } else{
                return this.l >= num.l ? TRUE : FALSE;
            }
        }
        throw new RuntimeException("不同类型无法比较大小");
    }

    /**
     *原地自增
     * @param num
     */
    public NumberValue increase(NumberValue num) {
        if (this.isInt) {
            if (num.isInt) {
                this.l = this.l + num.l;
            } else{
                this.l = this.l + num.d.longValue();
            }
        } else{
            this.d = this.d + (num.isInt ? num.l : num.d);
        }
        return this;
    }

    @Override
    public Value add(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return new NumberValue(left + right);
            } else{
                return new NumberValue(this.l + num.l);
            }
        }
        return CalculateValue.super.add(b);
    }

    @Override
    public Value sub(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return new NumberValue(left - right);
            } else{
                return new NumberValue(this.l - num.l);
            }
        }
        return CalculateValue.super.sub(b);
    }

    @Override
    public Value mul(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return new NumberValue(left * right);
            } else{
                return new NumberValue(this.l * num.l);
            }
        }
        return CalculateValue.super.mul(b);
    }

    @Override
    public Value div(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                if (right == 0.0) {
                    throw new RuntimeException("除数不能为0");
                }
                return new NumberValue(left / right);
            } else{
                if (num.l == 0.0) {
                    throw new RuntimeException("除数不能为0");
                }
                return new NumberValue(this.l / num.l);
            }
        }
        return CalculateValue.super.div(b);
    }

    @Override
    public Value unm() {
        if (isInt) {
            return new NumberValue(~l);
        }
        return new NumberValue(~(this.d.longValue()));
    }

    @Override
    public Value mod(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return new NumberValue(left % right);
            } else{
                return new NumberValue(this.l % num.l);
            }
        }
        return CalculateValue.super.mod(b);
    }

    @Override
    public Value intMod(Value b) {
        if (b instanceof NumberValue num) {
            long left = this.isInt ? this.l : this.d.longValue();
            long right = num.isInt ? num.l : num.d.longValue();
            return new NumberValue(left % right);
        }
        return CalculateValue.super.intMod(b);
    }

    @Override
    public Value pow(Value b) {
        if (b instanceof NumberValue num) {
            if (!this.isInt || !num.isInt) {
                double left = this.isInt ? l: d;
                double right = num.isInt ? num.l : num.d;
                return new NumberValue(Math.pow(left,right));
            } else{
                return new NumberValue(Math.pow(this.l,num.l));
            }
        }
        return CalculateValue.super.pow(b);
    }

    @Override
    public Value bitAnd(Value b) {
        if (b instanceof NumberValue num) {
            long left = this.isInt ? this.l : this.d.longValue();
            long right = num.isInt ? num.l : num.d.longValue();
            return new NumberValue(left & right);
        }
        return CalculateValue.super.bitAnd(b);
    }

    @Override
    public Value bitOr(Value b) {
        if (b instanceof NumberValue num) {
            long left = this.isInt ? this.l : this.d.longValue();
            long right = num.isInt ? num.l : num.d.longValue();
            return new NumberValue(left | right);
        }
        return CalculateValue.super.bitOr(b);
    }

    @Override
    public Value bitLeftMove(Value b) {
        if (b instanceof NumberValue num) {
            long left = this.isInt ? this.l : this.d.longValue();
            long right = num.isInt ? num.l : num.d.longValue();
            return new NumberValue(left << right);
        }
        return CalculateValue.super.bitLeftMove(b);
    }

    @Override
    public Value bitRightMove(Value b) {
        if (b instanceof NumberValue num) {
            long left = this.isInt ? this.l : this.d.longValue();
            long right = num.isInt ? num.l : num.d.longValue();
            return new NumberValue(left >> right);
        }
        return CalculateValue.super.bitRightMove(b);
    }

    public NumberValue negative(){
        if (isInt) {
            return new NumberValue(-1 * l);
        }
        return new NumberValue(-1 * d);
    }


    public boolean eqZero(){
        if (isInt) {
            return l == 0;
        }
        return d == 0;
    }
    public boolean leZero(){
        if (isInt) {
            return l <= 0;
        }
        return d <= 0;
    }
    public boolean gtZero(){
        if (isInt) {
            return l > 0;
        }
        return d > 0;
    }
    public boolean ltZero(){
        if (isInt) {
            return l < 0;
        }
        return d < 0;
    }

    public boolean geZero(){
        if (isInt) {
            return l >= 0;
        }
        return d >= 0;
    }
    public int intValue(){
        if (isInt) {
            return l.intValue();
        }
        return d.intValue();
    }

    public Number getValue() {
        if (isInt) {
            return l;
        }
        return d;
    }
}
