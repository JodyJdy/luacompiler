package com.jdy.lua.vm;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.Value;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jdy
 * @title: GlobalVal
 * @description:
 * @data 2023/9/18 16:40
 */
@Getter
public class GlobalVal {

    int index;
    private final String name;

    @Setter
    private Value val;

    public GlobalVal(int index, String name, Value val) {
        this.index = index;
        this.name = name;
        this.val = val;
    }

   public boolean isFunc(){
        return val.type() == DataTypeEnum.FUNCTION && val instanceof FuncInfo;
   }
   public int getFunGlobalIndex(){
        return ((FuncInfo)val).getGlobalFuncIndex();
   }


    @Override
    public String toString() {
        return "GlobalVal{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", val=" + val +
                '}';
    }
}
