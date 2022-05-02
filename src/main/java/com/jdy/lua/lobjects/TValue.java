package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.Objects;

import static com.jdy.lua.LuaConstants.*;

@Data
public class TValue extends Value {
    /**
     * 数据类型
     */
    int valueType;

    TbcList tbcList;

    public TValue(){

    }

    public void setFromTValue(TValue v){
        if(v != null) {
            this.setValueType(v.getValueType());
            this.setTbcList(v.getTbcList());
            this.setObj(v.getObj());
            this.setF(v.getF());
            this.setI(v.getI());
        }
    }

    @Data
    static class TbcList{
        Value value;
        int dataType;
        int delta;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TbcList tbcList = (TbcList) o;
            return dataType == tbcList.dataType &&
                    delta == tbcList.delta &&
                    Objects.equals(value, tbcList.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, dataType, delta);
        }
    }


    public static int novariant(int tt){
        return tt & 0x0F;
    }

    public static int  withvariant(int tt){
        return tt & 0x3F;
    }

    public static int ttype(TValue value){
        return withvariant(value.getValueType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TValue tValue = (TValue) o;
        return valueType == tValue.valueType && (tbcList == null || tbcList.equals(tValue.tbcList));

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), valueType, tbcList);
    }

    public static TValue strValue(String t){
        TValue v = new TValue();
        v.setValueType(LUA_TSTRING);
        v.setObj(t);
        return v;
    }
    public static TValue intValue(long t){
        TValue v = new TValue();
        v.setValueType(LUA_TNUMINT);
        v.setI(t);
        return v;
    }
    public static TValue doubleValue(double d){
        TValue v = new TValue();
        v.setValueType(LUA_TNUMFLONT);
        v.setF(d);
        return v;
    }
    public static TValue falseValue(){
        TValue v = new TValue();
        v.setValueType(LUA_TFALSE);
        return v;
    }
    public static TValue trueValue(){
        TValue v = new TValue();
        v.setValueType(LUA_TTRUE);
        return v;
    }

    public static TValue nilValue(){
        TValue v = new TValue();
        v.setValueType(LUA_TNIL);
        return v;
    }
}
