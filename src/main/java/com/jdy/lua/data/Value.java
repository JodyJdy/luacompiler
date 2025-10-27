package com.jdy.lua.data;

import java.io.*;

/**
 * @author jdy
 * @title: Value
 * @description:
 * @data 2023/9/14 16:26
 */
public interface Value extends Serializable {


    default byte[] serializeValue() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
            os.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    default Value deserializeValue(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try{
            ObjectInputStream is = new ObjectInputStream(bis);
            return (Value)is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }




    DataTypeEnum type();
    default BoolValue eq(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue ne(Value b){

        throw new RuntimeException("不支持比较");
    }
    default BoolValue lt(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue gt(Value b){
        throw new RuntimeException("不支持比较");
    }

    default BoolValue le(Value b){
        throw new RuntimeException("不支持比较");
    }
    default BoolValue ge(Value b){
        throw new RuntimeException("不支持比较");
    }

}
