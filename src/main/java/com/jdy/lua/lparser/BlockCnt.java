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
    int nactvar;
    boolean upval;
    boolean isloop;
    boolean insidetbc;
}
