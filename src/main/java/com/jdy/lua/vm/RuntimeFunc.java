package com.jdy.lua.vm;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.MultiValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时的函数
 *
 * @author jdy
 * @title: RuntimeFunc
 * @description:
 * @data 2023/9/20 10:55
 */
public class RuntimeFunc implements Value {
    final List<RuntimeUpVal> upValList = new ArrayList<>();

    /**
     * 对应的函数信息
     */
    final FuncInfo funcInfo;


    final RuntimeFunc parent;

    int used = -1;

    public int getFinalParamArg() {
        return finalParamArg;
    }

    /**
     * 最后一个参数的 寄存器索引
     */
    final int finalParamArg;

    final List<StackElement> registers = new ArrayList<>();


    public RuntimeFunc(FuncInfo funcInfo, RuntimeFunc parent) {
        this.funcInfo = funcInfo;
        this.parent = parent;
        this.finalParamArg = funcInfo.getUsed();
        init();
    }

    private void init() {
        //栈的数量进行调整，与FuncInfo 的最大值保持一致，不是当前的 funcInfo.used
        resetRegister(funcInfo.getRegisters().size());
        //栈的已经使用和funcInfo保持一致
        used = funcInfo.getUsed();
        //拷贝 UpVal
        for (UpVal upVal : funcInfo.getUpVal()) {
            StackElement runtime = getRuntimeUpValValue(upVal);
            upValList.add(new RuntimeUpVal(runtime, upValList.size()));
        }
    }

    public RuntimeFunc getParent() {
        return parent;
    }

    /**
     * 获取运行时的UpVal,funcInfo中的是编译时的，不能使用
     */
    public StackElement getRuntimeUpValValue(UpVal upVal) {
        int level = upVal.getLevel();
        // UpVal的值在对应函数中的下标
        int stackIndex = upVal.getUp().getIndex();
        RuntimeFunc target = parent;
        level -= 1;
        while (level != 0) {
            target = target.getParent();
            level--;
        }
        //获取到target
        return target.registers.get(stackIndex);
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.VM_RUNTIME_FUNC;
    }


    public int allocRegister() {
        used++;
        //只在需要扩容的时候扩容，用nil填充寄存器的值
        if (used == registers.size()) {
            registers.add(new StackElement(NilValue.NIL, used));
        }
        return used;
    }

    /**
     * 将寄存器数量调整到 n，如果不够，进行扩容
     */
    public void resetRegister(int n) {
       if (n > used) {
            while (used !=  n) {
                allocRegister();
            }
        }
        used = n;
    }

    /**
     * 初始化参数
     */
    public Value call(List<Value> values) {
        int i = 0;
        if (values.size() > 0) {
            if (funcInfo.isObjMethod) {
                registers.get(0).setValue(values.get(i));
                i++;
            }
            for (String ignored : funcInfo.paramNames) {
                if (i < values.size()) {
                    registers.get(i).setValue(values.get(i));
                }
                i++;
            }
            if (i < values.size() && funcInfo.hasMultiArg) {
                registers.get(i).setValue(new MultiValue(values.subList(i, values.size())));
            }
        }
        return Vm.execute(this);
    }


}
