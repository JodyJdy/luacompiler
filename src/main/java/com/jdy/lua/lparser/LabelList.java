package com.jdy.lua.lparser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LabelList {
    List<LabelDesc> arr = new ArrayList<>();

    public int getSize(){
        return arr.size();
    }
}
