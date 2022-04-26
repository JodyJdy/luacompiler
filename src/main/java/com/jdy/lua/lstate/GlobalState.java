package com.jdy.lua.lstate;

import com.jdy.lua.LuaConstants;
import com.jdy.lua.lobjects.TString;
import com.jdy.lua.lobjects.Table;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GlobalState {

    /**
     * 存放系统中所有字符串
     */
    Map<String, TString> strTable = new HashMap<>();
    Table[] mt = new Table[LuaConstants.LUA_NUMTYPES]; /** metatables for basic types */
}
