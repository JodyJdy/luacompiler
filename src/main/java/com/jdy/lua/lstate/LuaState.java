package com.jdy.lua.lstate;

import com.jdy.lua.lobjects.GcObject;
import com.jdy.lua.lobjects.StkId;
import com.jdy.lua.lobjects.UpVal;
import lombok.Data;

import java.util.List;

@Data
public class LuaState extends GcObject {
    int status;
    int allowhook;
    int nci;
    /**
     * 下一个栈的可用位置
     */
    StkId top;
    GlobalState globalState;
    CallInfo ci;
    StkId stack_last;
    StkId stack;
    List<UpVal> openupval;
    StkId tbcList;
    List<GcObject> gcList;
    CallInfo base_ci;
    int oldpc;
    int basehookcount;
    int hookcount;
}
