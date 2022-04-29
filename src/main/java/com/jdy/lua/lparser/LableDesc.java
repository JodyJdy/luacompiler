package com.jdy.lua.lparser;

import lombok.Data;

@Data
public class LableDesc {
    String name;
    int pc;  /** position in code */
    int line;  /** line where it appeared */
    int nactvar;  /** number of active variables in that position */
    boolean close;  /** goto that escapes upvalues */
}
