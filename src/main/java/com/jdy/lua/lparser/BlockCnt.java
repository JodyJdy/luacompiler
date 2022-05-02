package com.jdy.lua.lparser;

import lombok.Data;

/**
 * 表示一个Block
 */
@Data
public class BlockCnt {
    BlockCnt previous;
    int firstlabe;
    int firstgoto;
    /**
     * 记录了刚进去block时的变量数， 后面leave block时，需要使用到，
     * 所以 新建一个Block时， blockOuterActVars 的值等于 fs.blockOuterActVars
     */
    int blockOuterActVars;
    boolean upval;
    boolean isloop;
    boolean insidetbc;
}
