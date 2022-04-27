package com.jdy.lua;

import lombok.Data;

@Data
public class LuaConstants {

    /**
     * 数据类型
     */
    public static final int LUA_TNONE = -1;
    public static final int LUA_TNIL = 0;
    public static final int LUA_TBOOLEAN = 1;
    public static final int LUA_TLIGHTUSERDATA = 2;
    public static final int LUA_TNUMBER = 3;
    public static final int LUA_TSTRING = 4;
    public static final int LUA_TABLE = 5;
    public static final int LUA_TFUNCTION = 6;
    public static final int LUA_TUSERDATA = 7;
    public static final int LUA_THREAD = 8;
    public static final int LUA_NUMTYPES = 9;

    /**
     * 扩展 LUA_TBOOLEAN
     */
    public static final int LUA_TFALSE = LUA_TBOOLEAN;
    public static final int LUA_TTRUE = LUA_TBOOLEAN | 1 << 4;
    /**
     * 扩展 LUA_TNUMBER
     */
    public static final int LUA_TNUMINT = LUA_TNUMBER ;
    public static final int LUA_TNUMFLONT = LUA_TNUMBER | 1 << 4;
    /**
     * 运算符
     */
    public static final int LUA_OPADD = 0;
    public static final int LUA_OPSUB = 1;
    public static final int LUA_OPMUL = 2;
    public static final int LUA_OPMOD = 3;
    public static final int LUA_OPPOW = 4;
    public static final int LUA_OPDIV = 5;
    public static final int LUA_OPIDIV = 6;
    public static final int LUA_OPBAND = 7;
    public static final int LUA_OPBOR = 8;
    public static final int LUA_OPBXOR = 9;
    public static final int LUA_OPSHL = 10;
    public static final int LUA_OPSHR = 11;
    public static final int LUA_OPUNM = 12;
    public static final int LUA_OPBNOT = 13;


}
