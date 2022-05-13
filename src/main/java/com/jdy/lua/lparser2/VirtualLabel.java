package com.jdy.lua.lparser2;

import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.Instructions;
import com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration;
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
    public void addInstructionList(VirtualLabel label){
        if(this.equals(label)){
            return;
        }
        instructionList.addAll(label.getInstructionList());
        insPcs.addAll(label.getInsPcs());
    }

    /**
     * jump指令都跳转到pc1
     *  当执行到jmp时，pc在下一条，因此要-1
     */
    public void fixJump2Pc(int pc){
        for(int i=0;i<instructionList.size();i++){
            Instructions.setArgsJ(instructionList.get(i),pc-1 - insPcs.get(i));
        }
        instructionList.clear();
        insPcs.clear();
    }
}
