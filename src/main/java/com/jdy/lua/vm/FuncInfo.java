package com.jdy.lua.vm;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.StringValue;
import com.jdy.lua.data.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jdy
 * @title: Block
 * @description:
 * @data 2023/9/18 14:16
 */
public class FuncInfo implements Value {

    public void setObjMethod(boolean objMethod) {
        isObjMethod = objMethod;
    }

    /**
     * 是否是对象实例方法
     * a:b()这种
     */
    private boolean isObjMethod = false;
    /**
     * 只有 函数定义 和最外层的  block 拥有 codes
     */
    private final List<ByteCode> codes = new ArrayList<>();

    /**
     * 函数中使用到的label
     */
    private final Map<String, LabelMessage> labelLocation = new HashMap<>();

    /**
     * 引用的 父级 block 中的变量
     */
    private final List<UpVal> upVal = new ArrayList<>();
    private final Map<String, UpVal> upValMap = new HashMap<>();
    /**
     * block范围内的变量
     */
    private final Map<String, StackElement> localVarMap = new HashMap<>();
    /**
     * 全局范围变量
     * 不存在栈里面
     */
    private static final List<GlobalVal> globalVar = new ArrayList<>();
    private static final Map<String, GlobalVal> globalVarMap = new HashMap<>();

    /**
     * 全局 常量
     */
    private static final List<Value> constant = new ArrayList<>();

    /**
     * 当前block的寄存器
     */
    private final List<StackElement> registers = new ArrayList<>();

    private int used = -1;
    private FuncInfo parent;

    public FuncInfo() {
    }

    public FuncInfo(FuncInfo parent) {
        this.parent = parent;
    }

    /**
     * 申请一个寄存器
     */
    public int allocRegister() {
        used++;
        //用nil填充寄存器的值
        if (used == registers.size()) {
            registers.add(new StackElement(NilValue.NIL));
        }
        return used;
    }


    /**
     *释放指定的寄存器
     */
    public void freeRegisterWithIndex(int n) {
        if (n != used) {
            throw new RuntimeException("寄存器分配错误");
        }
        used--;
    }



    public void freeRegister() {
        if (used - 1 < 0) {
            throw new RuntimeException("没有足够的寄存器释放");
        }
        used--;
    }

    /**
     * 申请n个寄存器
     * 返回第一个申请的寄存器下标
     */
    public int allocRegister(int n) {
        int size = registers.size();
        for (int i = used + 1; i <= used + n; i++) {
            if (i >= size) {
                registers.add(new StackElement(NilValue.NIL));
            }
        }
        int s = used + 1;
        used += n;
        return s;
    }

    /**
     * 重置寄存器的使用
     * @param n
     */
    public void resetRegister(int n) {
        used = n;
    }
    /**
     * 释放n个寄存器
     */
    public void freeRegister(int n) {
        used -= n;
    }

    /**
     * 搜索变量
     */
    public StackElement searchVar(String name) {
        return localVarMap.get(name);
    }

    public StackElement searchParentVar(String name) {
        if (parent != null) {
            StackElement result;
            if ((result = parent.searchVar(name)) != null) {
                return result;
            }
            return parent.searchVar(name);
        }
        return null;
    }
    public int addVar(String name, Value val) {
        int reg = this.allocRegister();
        registers.set(reg, new StackElement(name, val, reg));
        return reg;
    }

    public UpVal searchUpVal(String name) {
        UpVal temp = upValMap.get(name);
        if (temp != null) {
            return temp;
        }
        StackElement val  = searchParentVar(name);
        if (val != null) {
            temp = new UpVal(val, upVal.size());
            upVal.add(temp);
            upValMap.put(name, temp);
        }
        return temp;
    }

    public static void addGlobalVal(String name, Value val) {
        GlobalVal globalVal = new GlobalVal(globalVar.size(), name, val);
        globalVar.add(globalVal);
        globalVarMap.put(name, globalVal);
    }
    public static int searchGlobalIndex(String name) {
        GlobalVal val = searchGlobal(name);
        if (val != null) {
            return val.index;
        }
        return -1;
    }
    public static GlobalVal searchGlobal(String name) {
        return globalVarMap.get(name);
    }

    /**
     * 这里使用 函数类型，没有专门定义新的类型
     */
    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.FUNCTION;
    }
    public void addCode(ByteCode byteCode) {
        codes.add(byteCode);
    }

    public static int getConstantIndex(String val) {
        return getConstantIndex(new StringValue(val));
    }
    public static int getConstantIndex(Value v) {
        for (int i = 0; i < constant.size(); i++) {
            if (constant.get(i).equals(v)) {
                return i;
            }
        }
        constant.add(v);
        return constant.size() - 1;
    }


    public void addLabel(String labelName) {
        labelLocation.put(labelName, new LabelMessage(codes.size(),used));
    }

    public int getNextPc(){
        return codes.size();
    }
    public LabelMessage getLabel(String labelName) {
        return labelLocation.get(labelName);
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    public void setHasMultiArg(boolean hasMultiArg) {
        this.hasMultiArg = hasMultiArg;
    }

    public void setRegisterVal(int reg, Value val) {
        registers.set(reg, new StackElement(val));
    }

    /**
     * 普通参数
     */

    protected List<String> paramNames = new ArrayList<>();
    /**
     * 结尾有 变长参数  ...这种
     */
    protected boolean hasMultiArg;


    public int getUsed() {
        return used;
    }
}
