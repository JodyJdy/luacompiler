package com.jdy.lua;

import lombok.Data;

@Data
public class LuaConstants {

    /**
     * 数据类型
     */
    public static int LUA_TNONE = -1;
    public static int LUA_TNIL = 0;
    public static int LUA_TBOOLEAN = 1;
    public static int LUA_TLIGHTUSERDATA = 2;
    public static int LUA_TNUMBER = 3;
    public static int LUA_TSTRING = 4;
    public static int LUA_TABLE = 5;
    public static int LUA_TFUNCTION = 6;
    public static int LUA_TUSERDATA = 7;
    public static int LUA_THREAD = 8;
    public static int LUA_NUMTYPES = 9;
    public static int LUA_TFALSE = LUA_TBOOLEAN;
    public static int LUA_TTRUE = LUA_TBOOLEAN | 1 << 4;
    /**
     * 运算符
     */
    public static int LUA_OPADD = 0;
    public static int LUA_OPSUB = 1;
    public static int LUA_OPMUL = 2;
    public static int LUA_OPMOD = 3;
    public static int LUA_OPPOW = 4;
    public static int LUA_OPDIV = 5;
    public static int LUA_OPIDIV = 6;
    public static int LUA_OPBAND = 7;
    public static int LUA_OPBOR = 8;
    public static int LUA_OPBXOR = 9;
    public static int LUA_OPSHL = 10;
    public static int LUA_OPSHR = 11;
    public static int LUA_OPUNM = 12;
    public static int LUA_OPBNOT = 13;


}
