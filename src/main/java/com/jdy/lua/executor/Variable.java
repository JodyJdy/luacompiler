package com.jdy.lua.executor;

import com.jdy.lua.data.Value;
import lombok.Data;

/**
 *
 * 变量
 * @author jdy
 * @title: Variable
 * @description:
 * @data 2023/9/14 16:35
 */
@Data
public class Variable {
    /**
     * 变量名称
     */
    private String name;
    /**
     * 变量值
     */
    private Value value;

    public Variable(String name) {
        this.name = name;
    }

    public Variable(String name, Value value) {
        this.name = name;
        this.value = value;
    }
}
