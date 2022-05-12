package com.jdy.lua.lparser2;

import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.Instructions;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于回填指令
 */
@Getter
public class VirtualLabel {
    /**
     * 存储 jump指令和jump指令的pc
     */
    private List<Instruction> instructionList = new ArrayList<>();
    private List<Integer> insPcs = new ArrayList<>();

    public void addInstruction(Instruction ins,int pc){
        instructionList.add(ins);
        insPcs.add(pc);
    }

    /**
     * jump指令都跳转到pc1
     */
    public void fixJump2Pc(int pc){
        for(int i=0;i<instructionList.size();i++){
            Instructions.setArgsJ(instructionList.get(i),pc - insPcs.get(i) - 1);
        }
    }
}
