package com.jdy.lua.lobjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonHeader {
    /**
     * 数据类型
     */
    int tt;
    /**
     * gc相关的标记位
     */
    int marked;
}
