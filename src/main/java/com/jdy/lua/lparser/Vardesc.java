package com.jdy.lua.lparser;

import com.jdy.lua.lobjects.TValue;
import lombok.Data;

/**
 * 描述 local var
 */
@Data
public class Vardesc {
    TValue value;
    int kind;
    int ridx;  /* register holding the variable */
    // 本地变量表索引
    int pidx;  /* indexForTable of the variable in the Proto's 'locvars' array */
    //变量名
    String name;  /* variable name */
    TValue k; /* constant value */
}
