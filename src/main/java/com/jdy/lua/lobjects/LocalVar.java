package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class LocalVar {
    String name;
    int startpc;
    int endpc;
}
