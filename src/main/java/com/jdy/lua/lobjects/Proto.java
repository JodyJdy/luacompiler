package com.jdy.lua.lobjects;

import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lstate.LuaState;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Proto extends GcObject {
    int numparams;
    boolean isVararg;
    int maxStackSize;
    int linedefined;  /** debug information  */
    int lastlinedefined;  /** debug information  */
    List<TValue> constants = new ArrayList<>(); /** constants used by the function */
    List<Instruction> code = new ArrayList<>();
    List<Proto> protoList = new ArrayList<>(); /** functions defined inside the function */
    List<UpvalDesc> upvalues = new ArrayList<>(); /**  upvalue information */
    /**
     * 存放行号的
     */
    List<Integer> lineInfoss = new ArrayList<>();
  ;
    List<LocalVar> localVars = new ArrayList<>(); /** information about local variables (debug information) */
    String source;


    public void addLineInfo(int i){
        lineInfoss.add(i);
    }
    public Integer getLineInfo(int i){
        return lineInfoss.get(i);
    }

    public Instruction getInstruction(int i){
        return code.get(i);
    }
    public void setInstruction(int i,Instruction ins){
        code.set(i,ins);
    }

    public void addLocalVar(LocalVar var){
        localVars.add(var);
    }
    public LocalVar getLocalVar(int i){
        return localVars.get(i);
    }

    public UpvalDesc getUpValDesc(int i){
        return upvalues.get(i);
    }
    public void addUpValDesc(UpvalDesc desc){
        upvalues.add(desc);
    }

    public int getLocaVarSize(){
        return localVars.size();
    }
    public static Proto newProto(LuaState l){
        Proto p = new Proto();
        return p;
    }

    public void addConstants(TValue k){
        constants.add(k);
    }



}
