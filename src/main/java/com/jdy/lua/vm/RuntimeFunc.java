package com.jdy.lua.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运行时的函数
 * @author jdy
 * @title: RuntimeFunc
 * @description:
 * @data 2023/9/20 10:55
 */
public class RuntimeFunc {
    private final List<UpVal> upVal = new ArrayList<>();
    private final List<ByteCode> codes = new ArrayList<>();

    private int used = -1;

    private final List<StackElement> registers = new ArrayList<>();
    private final Map<String, StackElement> localVarMap = new HashMap<>();


    private boolean isObjMethod = false;
    /**
     * 只有 函数定义 和最外层的  block 拥有 codes
     */
}
