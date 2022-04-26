package com.jdy.lua.lobjects;

import lombok.Data;

import java.util.List;

@Data
public class LClosure extends ClosureHeader implements Closure {
    Proto proto;
    List<UpVal> upVals;
}
