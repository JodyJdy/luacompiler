package com.jdy.lua.vm;

import com.jdy.lua.data.*;

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

    static {
        constant.add(BoolValue.TRUE);
        constant.add(BoolValue.FALSE);
        constant.add(NilValue.NIL);
    }


    /**
     * 当前block的寄存器
     */
    private final List<StackElement> registers = new ArrayList<>();


    /**
     * 存储全局的函数，采用常量的方式粗粒函数
     */
    private final static List<FuncInfo> allFuncs = new ArrayList<>();


    private int used = -1;
    private FuncInfo parent;

    private FuncInfo(int globalFuncIndex,FuncInfo parent) {
        this.globalFuncIndex = globalFuncIndex;
        this.parent = parent;
    }

    private FuncInfo(int globalFuncIndex) {
        this.globalFuncIndex = globalFuncIndex;
    }
    /**
     * 当前函数的全局索引
     */
    private final int globalFuncIndex;

    public static FuncInfo createFunc(){
        FuncInfo funcInfo = new FuncInfo(FuncInfo.allFuncs.size());
        allFuncs.add(funcInfo);
        return funcInfo;
    }

    public static FuncInfo createFunc(FuncInfo parent) {
        FuncInfo funcInfo = new FuncInfo(FuncInfo.allFuncs.size(),parent);
        allFuncs.add(funcInfo);
        return funcInfo;
    }

    /**
     * 申请一个寄存器
     */
    public int allocRegister() {
        used++;
        //用nil填充寄存器的值
        if (used == registers.size()) {
            registers.add(new StackElement(NilValue.NIL,used));
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



    /**
     * 申请n个寄存器
     * 返回第一个申请的寄存器下标
     */
    public int allocRegister(int n) {
        int size = registers.size();
        for (int i = used + 1; i <= used + n; i++) {
            if (i >= size) {
                registers.add(new StackElement(NilValue.NIL,i));
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
        StackElement var = new StackElement(name, val, reg);
        registers.set(reg, var);
        localVarMap.put(name, var);
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

    public static int  addGlobalVal(String name, Value val) {
        int index = globalVar.size();
        GlobalVal globalVal = new GlobalVal(index, name, val);
        globalVar.add(globalVal);
        globalVarMap.put(name, globalVal);
        return index;
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

    public static Value getConstant(int index) {
        return constant.get(index);
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

    public boolean isObjMethod() {
        return isObjMethod;
    }

    public List<ByteCode> getCodes() {
        return codes;
    }

    public Map<String, LabelMessage> getLabelLocation() {
        return labelLocation;
    }

    public List<UpVal> getUpVal() {
        return upVal;
    }

    public Map<String, UpVal> getUpValMap() {
        return upValMap;
    }

    public Map<String, StackElement> getLocalVarMap() {
        return localVarMap;
    }

    public List<StackElement> getRegisters() {
        return registers;
    }

    public FuncInfo getParent() {
        return parent;
    }

    public List<String> getParamNames() {
        return paramNames;
    }

    public boolean isHasMultiArg() {
        return hasMultiArg;
    }


    public void showDebug(){
        System.out.println("-------------- 常量池----------------");
        System.out.println(FuncInfo.constant);
        System.out.println("------------ 当前寄存器信息-----------------");
        for (int i = 0; i < registers.size(); i++) {
            System.out.println(registers.get(i));
        }
        //global
        System.out.println("-----------全局变量 ---------");
        for (GlobalVal globalVal : globalVar) {
            System.out.println(globalVal);
        }
        //upval
        System.out.println("----------upval---------");
        upVal.forEach(System.out::println);
        //字节码
        this.fillJMP();
        System.out.println("--------byte code ------");
        for (int i = 0; i < codes.size(); i++) {
            System.out.println("pc=  "+i+"  "  +codes.get(i));
        }
    }

    /**
     * 填充jmp指令的调整位置
     */
    public void fillJMP(){
        codes.forEach(code->{
            if (code instanceof ByteCode.JMP jmp) {
                jmp.applyLabel();
            }
        });
    }

    public static GlobalVal getGlobalVal(int index) {
        return globalVar.get(index);
    }

    public int getGlobalFuncIndex() {
        return globalFuncIndex;
    }
    public static List<FuncInfo>  funcInfos(){
        return allFuncs;
    }


    /**
     * 调用 当前函数
     * @param values
     * @return
     */
    public Value call(List<Value> values) {
        int i = 0;
        if (values.size() > 0) {
            if (this.isObjMethod) {
                registers.get(i).setValue(values.get(i));
                i++;
            }
            for (String param : paramNames) {
                if (i < values.size()) {
                    registers.get(i).setValue(values.get(i));
                }
                i++;
            }
            if (i < values.size() && this.hasMultiArg) {
               registers.get(i) .setValue(new MultiValue(values.subList(i,values.size())));
            }
        }
        return Vm.execute(this);
    }

}