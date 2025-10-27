package com.jdy.lua.vm;

import com.jdy.lua.data.*;
import lombok.Getter;
import lombok.Setter;

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
    boolean isObjMethod = false;
    /**
     * 只有 函数定义 和最外层的  block 拥有 codes
     */
    @Getter
    final List<ByteCode> codes = new ArrayList<>();

    /**
     * 函数中使用到的label
     */
    final Map<String, LabelMessage> labelLocation = new HashMap<>();

    /**
     * 引用的 父级 block 中的变量
     */
    @Getter
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
        //初始化默认常量
        constant.add(BoolValue.TRUE);
        constant.add(BoolValue.FALSE);
        constant.add(NilValue.NIL);
    }


    /**
     * 当前block的寄存器
     */
    @Getter
    private final List<StackElement> registers = new ArrayList<>();


    /**
     * 存储全局的函数，采用类似常量的方式处理函数
     */
    private final static List<FuncInfo> allFuncs = new ArrayList<>();


    /**
     * 寄存器的使用情况 （指令生成时）
     */
    @Getter
    private int used = -1;
    private FuncInfo parent;

    private FuncInfo(int globalFuncIndex, FuncInfo parent) {
        this.globalFuncIndex = globalFuncIndex;
        this.parent = parent;
    }

    private FuncInfo(int globalFuncIndex) {
        this.globalFuncIndex = globalFuncIndex;
    }

    /**
     * 当前函数的全局索引
     */
    @Getter
    private final int globalFuncIndex;

    public static FuncInfo createFunc() {
        FuncInfo funcInfo = new FuncInfo(FuncInfo.allFuncs.size());
        allFuncs.add(funcInfo);
        return funcInfo;
    }

    public static FuncInfo createFunc(FuncInfo parent) {
        FuncInfo funcInfo = new FuncInfo(FuncInfo.allFuncs.size(), parent);
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
            registers.add(new StackElement(NilValue.NIL, used));
        }
        return used;
    }


    /**
     * 释放指定的寄存器
     */
    public void freeRegisterWithIndex(int n) {
        if (n != used) {
            throw new RuntimeException("寄存器分配错误");
        }
        if (registers.get(n).isLocalVar()) {
            localVarMap.remove(registers.get(n).getVarName());
        }
        used--;
    }


    /**
     * 重置寄存器的使用
     *
     * @param n
     */
    public void resetRegister(int n) {
        //将其中的变量进行删除
        if (n < used) {
            for (int i = n + 1; i <= used; i++) {
                StackElement elem = registers.get(i);
                if (elem.isLocalVar()) {
                    localVarMap.remove(elem.getVarName());
                }
            }

        } else if (n > used) {
            while (used != n) {
                allocRegister();
            }
        }
        used = n;
    }

    /**
     * 搜索变量
     */
    public StackElement searchVar(String name) {
        return localVarMap.get(name);
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
        FuncInfo tempParent = parent;
        StackElement result = null;
        int level = 1;
        while (tempParent != null) {
            if ((result = tempParent.searchVar(name)) != null) {
                break;
            }
            level++;
            tempParent = tempParent.parent;
        }
        if (result != null) {
            temp = new UpVal(result, upVal.size(), level);
            upVal.add(temp);
            upValMap.put(name, temp);
        }
        return temp;
    }

    public static int addGlobalVal(String name, Value val) {
        int index = globalVar.size();
        GlobalVal globalVal = new GlobalVal(index, name, val);
        globalVar.add(globalVal);
        globalVarMap.put(name, globalVal);
        return index;
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
        labelLocation.put(labelName, new LabelMessage(codes.size(), used));
    }

    public int getNextPc() {
        return codes.size();
    }

    public LabelMessage getLabel(String labelName) {
        return labelLocation.get(labelName);
    }

    /**
     * 普通参数
     */

    @Setter
    protected List<String> paramNames = new ArrayList<>();
    /**
     * 结尾有 变长参数  ...这种
     */
    @Setter
    protected boolean hasMultiArg;


    public boolean isObjMethod() {
        return isObjMethod;
    }




    public static void showGlobal() {
        System.out.println("-------------- 常量池----------------");
        System.out.println(FuncInfo.constant);
        //global
        System.out.println("-----------全局变量 ---------");
        for (GlobalVal globalVal : globalVar) {
            System.out.println(globalVal);
        }
    }

    public void showDebug() {
        System.out.println("------------ 当前寄存器信息-----------------");
        for (StackElement register : registers) {
            System.out.println(register);
        }
        //upval
        System.out.println("----------upval---------");
        upVal.forEach(System.out::println);
        //字节码
        System.out.println("--------byte code ------");
        for (int i = 0; i < codes.size(); i++) {
            System.out.println("pc=  " + i + "  " + codes.get(i));
        }
    }

    /**
     *
     * 填充jmp指令的跳转位置
     */
    public static void fillJMP() {
        FuncInfo.allFuncs.forEach(funcInfo -> {
            funcInfo.codes.forEach(code -> {
                if (code instanceof ByteCode.Jmp jmp) {
                    jmp.applyLabel();
                }
            });
        });
    }

    public static GlobalVal getGlobalVal(int index) {
        return globalVar.get(index);
    }

    public static List<FuncInfo> funcInfos() {
        return allFuncs;
    }


}
