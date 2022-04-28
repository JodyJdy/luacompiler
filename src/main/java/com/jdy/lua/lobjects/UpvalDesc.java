package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class UpvalDesc {
    TString name;
    boolean instack;
    int idx;
    int kind;
}
