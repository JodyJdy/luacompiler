package com.jdy.lua.lparser2;

import com.jdy.lua.lobjects.LocalVar;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<LocalVar> localVars = new ArrayList<>();
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
}
