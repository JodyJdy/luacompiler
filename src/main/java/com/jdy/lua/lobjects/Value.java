package com.jdy.lua.lobjects;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class Value extends GcObject {
    /**
     * light userdata
     */
    Object p;
    /**
     * 整数
     */
    long i;
    /**
     * 浮点数
     */
    double n;

    public Value(Object p) {
        this.p = p;
    }

    public Value(long i) {
        this.i = i;
    }

    public Value(double n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Value value = (Value) o;
        return i == value.i &&
                Double.compare(value.n, n) == 0 &&
                p.equals(value.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), p, i, n);
    }
}
