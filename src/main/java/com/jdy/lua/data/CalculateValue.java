package com.jdy.lua.data;

/**
 * 支持运算的 Value
 */
public interface CalculateValue extends Value{
    default Value add(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持+运算");
    }
    default Value sub(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持-运算");
    }
    default Value mul(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持*运算");
    }
    default Value div(Value b){

        throw new RuntimeException(b.getClass().getName() + "不支持-运算");
    }
    default Value unm(){
        throw new RuntimeException("不支持~运算");
    }
    default Value mod(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持%运算");
    }

    default Value intMod(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持//运算");
    }
    default Value pow(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持^运算");
    }
    default Value concat(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持..运算");
    }

    default Value bitAnd(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持&运算");
    }


    default Value bitOr(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持|运算");
    }


    /**
     * <<
     */
    default Value bitLeftMove(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持<<运算");
    }

    default Value bitRightMove(Value b){
        throw new RuntimeException(b.getClass().getName() + "不支持>>运算");
    }


    default Value len(){
        throw new RuntimeException(this.getClass().getName() + "不支持>>运算");
    }


}
