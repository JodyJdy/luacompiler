package com.jdy.lua.lobjects;

import com.jdy.lua.lopcodes.Instruction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Proto extends GcObject {
    int numparams;
    boolean isVararg;
    int maxstacksize;
    int sizeupvalues;  /** size of 'upvalues' */
    int sizek;  /** size of 'k' */
    int sizecode;
    int sizelineinfo;
    int sizep;  /** size of 'p' */
    int sizelocvars;
    int sizeabslineinfo;  /** size of 'abslineinfo' */
    int linedefined;  /** debug information  */
    int lastlinedefined;  /** debug information  */
    List<TValue> k = new ArrayList<>(); /** constants used by the function */
    List<Instruction> code = new ArrayList<>();
    List<Proto> protoList = new ArrayList<>(); /** functions defined inside the function */
    List<UpvalDesc> upvalues = new ArrayList<>(); /**  upvalue information */
    /**
     * 存放行号的
     */
    List<Integer> lineInfoss = new ArrayList<>();
    /**
     * 没有使用到
     */
    List<AbsLineInfo> absLineInfos = new ArrayList<>();
    List<LocalVar> localVars = new ArrayList<>(); /** information about local variables (debug information) */
    TString source;
    List<GcObject> gcList = new ArrayList<>();

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

}
