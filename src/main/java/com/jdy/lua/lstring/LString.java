package com.jdy.lua.lstring;

import com.jdy.lua.lstate.GlobalState;
import com.jdy.lua.lstate.LuaState;

public class LString {

    public static String newStr(LuaState luaState, String str){
        GlobalState globalState = luaState.getGlobalState();
        if(globalState == null){
            globalState = new GlobalState();
            luaState.setGlobalState(globalState);
        }
        globalState.getStrSet().add(str);
        return str;
    }
}
