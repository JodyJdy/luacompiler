package com.jdy.lua.lobjects;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Value extends GcObject implements Cloneable {
    /**
     * light userdata
     */
    Object obj;
    /**
     * 整数
     */
    long i;
    /**
     * 浮点数
     */
    double f;

    public Value(Object obj) {
        this.obj = obj;
    }

    public Value(long i) {
        this.i = i;
    }

    public Value(double f) {
        this.f = f;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Value value = (Value) o;
        return i == value.i &&
                Double.compare(value.f, f) == 0 &&
                (obj ==null ||  obj.equals(value.obj));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), obj, i, f);
    }


}
