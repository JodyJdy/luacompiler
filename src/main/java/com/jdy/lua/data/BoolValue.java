package com.jdy.lua.data;

import com.jdy.lua.statement.Expr;
import lombok.Data;

/**
 * @author jdy
 * @title: BoolValue
 * @description:
 * @data 2023/9/14 16:28
 */
@Data
public class BoolValue implements Value, Expr {
    public static BoolValue TRUE = new BoolValue(true);
    public static BoolValue FALSE = new BoolValue(false);
    public BoolValue(Boolean b) {
        this.b = b;
    }

    public BoolValue() {
    }

    Boolean b;

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.BOOLEAN;
    }
}
