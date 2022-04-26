package com.jdy.lua.lobjects;

import com.jdy.lua.lopcodes.Instruction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Proto extends GcObject {
    int numparams;
    int is_vararg;
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
    List<Proto> p = new ArrayList<>(); /** functions defined inside the function */
    List<UpvalDesc> upvalues = new ArrayList<>(); /**  upvalue information */
    List<Integer> lineinfo = new ArrayList<>();
    List<AbsLineInfo> absLineInfos = new ArrayList<>();
    List<LocalVar> localVars = new ArrayList<>(); /** information about local variables (debug information) */
    TString source;
    List<GcObject> gcList = new ArrayList<>();

}
