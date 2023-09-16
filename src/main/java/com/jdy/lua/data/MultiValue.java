package com.jdy.lua.data;

import lombok.Data;

import java.util.List;

@Data
public class MultiValue implements Value{
    public MultiValue(List<Value> valueList) {
        this.valueList = valueList;
    }

    private final List<Value> valueList;
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.MULTI;
    }
}
