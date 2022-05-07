package com.jdy.lua.lparser2;

import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter
public class FunctionInfo {
    /**
     * 已经使用的寄存器数量
     */
    private int usedRegs;
    /**
     * 作用域的层级
     */
    private int scopeLevel;
    /**
     * 本地变量
     */
    private List<LocVarInfo> localVars = new ArrayList<>();
    private Map<String,LocVarInfo> locVarInfoMap = new HashMap<>();
    /**
     * UpValues
     */
    private Map<String, UpvalInfo> upValMap = new HashMap<>();
    /**
     * 常量
     */
    private Map<TValue,Integer> constants = new HashMap<>();
    /**
     * 未处理的break语句
     */
    private List<List<Integer>> breaks = new ArrayList<>();
    private FunctionInfo parent;
    private List<Instruction> instructions = new ArrayList<>();
    int line;
    int lastLine;
    int numParams;
    boolean isVararg;

    public FunctionInfo(){
        isVararg = false;
        numParams = 0;
    }

    public FunctionInfo(FunctionInfo parent, int numParam,boolean isVararg){
        this.parent = parent;
        this.numParams = numParam;
        this.isVararg = isVararg;
    }

    public int getUsedRegs(){
        return usedRegs;
    }

    public void setUsedRegs(int usedRegs) {
        this.usedRegs = usedRegs;
    }

    public int allocReg(){
        usedRegs++;
        return usedRegs - 1;
    }

    public void freeReg(){
        usedRegs--;
    }
    public int allocReg(int n){
        usedRegs+=n;
        return usedRegs-n;
    }
    public void freeReg(int n){
        usedRegs-=n;
    }

    public int addInstruction(Instruction ins){
        instructions.add(ins);
        return instructions.size() - 1;
    }
    public Instruction getInstruction(int pc){
        return instructions.get(pc);
    }

    public int indexOfConstant(TValue k) {
        Integer idx = constants.get(k);
        if (idx != null) {
            return idx;
        }
        idx = constants.size();
        constants.put(k, idx);
        return idx;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public int getPc(){
        return instructions.size() - 1;
    }

    /**
     * 移除变量
     */
    public void removeLocVar(LocVarInfo varInfo) {
        freeReg();
        if (varInfo.getPrev() == null) {
            locVarInfoMap.remove(varInfo.getName());
        } else if (varInfo.getPrev().getScopeLv() == varInfo.getScopeLv()) {
            removeLocVar(varInfo.getPrev());
        } else {
            locVarInfoMap.put(varInfo.getName(),varInfo.getPrev());
        }
    }

    /**
     * 添加一个local变量
     */
    public int addLocVar(String name,int startPc){
        LocVarInfo newLocal = new LocVarInfo();
        newLocal.setName(name);
        newLocal.setPrev(locVarInfoMap.get(name));
        newLocal.setSlot(allocReg());
        newLocal.setScopeLv(scopeLevel);
        newLocal.setStartPC(startPc);
        locVarInfoMap.put(name,newLocal);
        return newLocal.getSlot();
    }

    /**
     * 获取变量的寄存器位置
     */
    public int slotOfLocVar(String name) {
        return locVarInfoMap.containsKey(name)
                ? locVarInfoMap.get(name).getSlot()
                : -1;
    }

    /**
     * 获取 UpVal的位置
     */
    public int indexOfUpval(String name) {
        if (upValMap.containsKey(name)) {
            return upValMap.get(name).getIndex();
        }
        if (parent != null) {
            if (parent.getLocVarInfoMap().containsKey(name)) {
                LocVarInfo locVar = parent.getLocVarInfoMap().get(name);
                int idx =upValMap.size();
                UpvalInfo upval = new UpvalInfo();
                //locVarSlot，是upVal对应的locVar的slot值
                upval.locVarSlot = locVar.slot;
                upval.upvalIndex = -1;
                //upVal的Index在当前FunctionInfo中的下标
                upval.index = idx;
                upValMap.put(name, upval);
                locVar.captured = true;
                return idx;
            }
            int uvIdx = parent.indexOfUpval(name);
            if (uvIdx >= 0) {
                int idx = upValMap.size();
                UpvalInfo upval = new UpvalInfo();
                //这里没有设置lovVarSlot
                upval.locVarSlot = -1;
                upval.upvalIndex = uvIdx;
                upval.index = idx;
                upValMap.put(name, upval);
                return idx;
            }
        }
        return -1;
    }
}
