package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class UpvalDesc {
    String  name;
    boolean instack;
    int idx;
    int kind;
}
