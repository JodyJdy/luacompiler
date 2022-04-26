package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class LocalVar {
    TString name;
    int startpc;
    int endpc;
}
