package com.jdy.lua.lobjects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TValue extends Value {
    /**
     * 数据类型
     */
    int tt;

    TbcList tbcList;



    @Data
    static class TbcList{
        Value value;
        int tt;
        int delta;
    }

    public static int novariant(int tt){
        return tt & 0x0F;
    }

    public static int  withvariant(int tt){
        return tt & 0x3F;
    }

    public static int ttype(TValue value){
        return withvariant(value.getTt());
    }

}
