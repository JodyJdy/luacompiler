package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.List;
@Data
public class Udata extends GcObject {
    int nuvalue;
    int len;
    List<GcObject> gcObjectList;
    TValue uv;
}
