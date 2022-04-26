package com.jdy.lua.lstring;

import com.jdy.lua.lobjects.TString;
import com.jdy.lua.lstate.GlobalState;
import com.jdy.lua.lstate.LuaState;

import java.util.Map;

public class LString {

    public static TString newStr(LuaState luaState, String str){
        GlobalState globalState = luaState.getGlobalState();
        Map<String,TString> stringMap = globalState.getStrTable();
        if(stringMap.containsKey(str)){
            return stringMap.get(str);
        }
        TString string = new TString(str);
        stringMap.put(str,string);
        return string;
    }
}
