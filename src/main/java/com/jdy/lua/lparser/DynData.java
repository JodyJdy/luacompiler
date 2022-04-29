package com.jdy.lua.lparser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class DynData {

    /** list of all active local variables */
    List<Vardesc> arr = new ArrayList<>();
    int n;
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
        n-=num;
    }
}
