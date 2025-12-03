package com.jdy.lua.data;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class MultiValue implements Value{
    public MultiValue(List<Value> valueList) {
        this.valueList = valueList;
    }

    public static MultiValue of(Value... values) {
        return new MultiValue(List.of(values));
    }
    public static MultiValue of(List<Value> valueList) {
        return new MultiValue(valueList);
    }

    private final List<Value> valueList;
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.MULTI;
    }
    @Override
    public String toString(){
       return valueList.toString();
    }

    @Override
    public BoolValue eq(Value b) {
        if (b instanceof MultiValue m) {
            if (m.valueList.size() != this.valueList.size()) {
                return  BoolValue.FALSE;
            }
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.get(i).eq(m.valueList.get(i)) == BoolValue.FALSE) {
                    return BoolValue.FALSE;
                }
            }
            return BoolValue.TRUE;
        }
        return BoolValue.FALSE;
    }

    @Override
    public BoolValue ne(Value b) {
        return eq(b) == BoolValue.TRUE ? BoolValue.FALSE : BoolValue.TRUE;
    }

    @Override
    public BoolValue lt(Value b) {
        throw new RuntimeException("MultiValue无法比较大小");
    }

    @Override
    public BoolValue gt(Value b) {
        throw new RuntimeException("MultiValue无法比较大小");
    }

    @Override
    public BoolValue le(Value b) {
        throw new RuntimeException("MultiValue无法比较大小");
    }

    @Override
    public BoolValue ge(Value b) {
        throw new RuntimeException("MultiValue无法比较大小");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MultiValue that = (MultiValue) o;
        return Objects.equals(valueList, that.valueList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valueList);
    }
}
