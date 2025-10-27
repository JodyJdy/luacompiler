package com.jdy.lua.vm;

import com.jdy.lua.data.DataTypeEnum;
import com.jdy.lua.data.MultiValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Value;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
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
     * 执行当前函数的vm类
     */
    @Getter
    transient Vm vm;

    /**
     * 对应的函数信息
     */
    final FuncInfo funcInfo;


    @Getter
    final RuntimeFunc parent;

    int used = -1;

    /**
     * 最后一个参数的 寄存器索引
     */
    final int finalParamArg;
     StackElement[] registers;

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
        return target.registers[stackIndex];
    }

    @Override
    public DataTypeEnum type() {
        return DataTypeEnum.VM_RUNTIME_FUNC;
    }


    public int allocRegister() {
        used++;
        if (registers == null) {
            registers = new StackElement[0];
        }
        //只在需要扩容的时候扩容，用nil填充寄存器的值
        if (used == registers.length) {
            registers =  Arrays.copyOf(registers, registers.length + 5);
            for (int i = used; i < registers.length;i++) {
                registers[i] = new StackElement(NilValue.NIL,i);
            }
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
        if (!values.isEmpty()) {
            if (funcInfo.isObjMethod) {
                registers[0].setValue(values.get(i));
                i++;
            }
            for (String ignored : funcInfo.paramNames) {
                if (i < values.size()) {
                    registers[i].setValue(values.get(i));
                }
                i++;
            }
            if (i < values.size() && funcInfo.hasMultiArg) {
                registers[i].setValue(new MultiValue(values.subList(i, values.size())));
            }
        }
        return new Vm(this).execute();
    }


}
