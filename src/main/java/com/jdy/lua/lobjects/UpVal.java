package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.List;

@Data
public class UpVal extends GcObject {
    int tbc;
    TValue v;


    UpVal next;
    List<UpVal> previous;
    TValue value;
}
