package com.jdy.lua.lparser;

/**
 * 表示一个Block
 */
public class BlockCnt {
    BlockCnt previous;
    int firstlabe;
    int firstgoto;
    int nactvar;
    int upval;
    boolean isloop;
    boolean insidetbc;
}
