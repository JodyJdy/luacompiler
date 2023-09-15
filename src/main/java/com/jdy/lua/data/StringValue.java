package com.jdy.lua.data;

import com.jdy.lua.statement.Expr;
import lombok.Data;

/**
 * @author jdy
 * @title: StringValue
 * @description:
 * @data 2023/9/14 16:27
 */
@Data
public class StringValue implements Value, Expr {
    private String val;

    public StringValue() {
    }

    public StringValue(String val) {
        this.val = val;
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.STRING;
    }
}
