package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.List;

@Data
public class ClosureHeader extends GcObject {
    int nupvalues;
    List<GcObject> gcObjectList;
}
