package com.jdy.lua.lstate;

import com.jdy.lua.LuaConstants;
import com.jdy.lua.lobjects.Table;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GlobalState {

    /**
     * 存放系统中所有字符串
     */
    Set<String> strSet = new HashSet<>();
    Table[] mt = new Table[LuaConstants.LUA_NUMTYPES]; /** metatables for basic types */
}
