package com.jdy.lua.lobjects;

import lombok.Data;

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
    }

    public TString(String contents, int extra) {
        this(contents);
        this.extra =extra;
    }
}
