package com.jdy.lua.lobjects;

import lombok.Data;

@Data
public class UpvalDesc {
    TString name;
    int instack;
    int idx;
    int kind;
}
