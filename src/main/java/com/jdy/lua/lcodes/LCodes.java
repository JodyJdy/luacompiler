package com.jdy.lua.lcodes;

import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lparser.FuncState;

public class LCodes {


    int luaK_code(FuncState fs, Instruction i){
        Proto proto = fs.getF();
        proto.getCode().add(i);
        fs.setPc(fs.getPc() + 1);
        return 0;
    }
}
