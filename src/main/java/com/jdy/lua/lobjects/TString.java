package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.Objects;

@Data
public class TString extends GcObject {
    /**
     * 表示长字符串是否有hash值（这里统一使用string表示）；保留字符串数组中的下标
     */
    int extra;
    int hash;
    String contents;

    public TString(String contents) {
        this.contents = contents;
        this.hash = contents.hashCode();
    }

    public TString(String contents, int extra) {
        this(contents);
        this.extra =extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TString string = (TString) o;
        return extra == string.extra &&
                hash == string.hash &&
                Objects.equals(contents, string.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extra, hash, contents);
    }
}
