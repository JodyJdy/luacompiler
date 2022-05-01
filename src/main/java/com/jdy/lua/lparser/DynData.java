package com.jdy.lua.lparser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析器使用的动态数据
 *   arr存放了所有运时解析到的所有变量
 *   每一个 FuncState拥有的变量是 DynData的一部分。
 */
@Data
public class DynData {

    /** list of all active local variables */
    List<Vardesc> arr = new ArrayList<>();
    int size;

    LabelList gt = new LabelList();  /* list of pending gotos */
    LabelList label = new LabelList();   /* list of active labels */


    public Vardesc getVarDesc(int i){
        return arr.get(i);
    }
    public void addVarDesc(Vardesc v){
        arr.add(v);
    }

    public void removeNumVarDesc(int num){
        for(int i=0;i<num;i++){
            arr.remove(arr.size() - 1);
        }
    }

    public int getActiveLocVarSize(){
        return arr.size();
    }
}
