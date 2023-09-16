package com.jdy.lua.luanative;

import com.jdy.lua.executor.Block;

public class NativeLoader {
    /**
     * 加载需要的模块
     */
    public static void load(){
        Block.addNative(Math.TABLE_NAME,Math.MATH);
        Block.addNative("print",PrintMethod.print());
        Block.addNative("println",PrintMethod.println());
        Block.addNative("setmetatable",MetaTableMethod.setmetatable());
        Block.addNative("getmetatable",MetaTableMethod.getmetatable());
    }
}
