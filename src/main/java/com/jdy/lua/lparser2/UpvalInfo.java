package com.jdy.lua.lparser2;

import lombok.Data;

@Data
public class UpvalInfo {
    int locVarSlot;
    int upvalIndex;
    int index;
}
