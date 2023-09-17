package com.jdy.lua.luanative;

import com.jdy.lua.executor.Block;

public class NativeLoader {
    /**
     * 加载需要的模块
     */
    public static void load(){
        Block.addNative(Math.TABLE_NAME,Math.MATH);
        Block.addNative("print",GlobalMethod.print());
        Block.addNative("println",GlobalMethod.println());
        Block.addNative("type",GlobalMethod.type());
        Block.addNative("setmetatable",GlobalMethod.setmetatable());
        Block.addNative("getmetatable",GlobalMethod.getmetatable());
    }
}
