package com.jdy.lua.luanative;

import com.jdy.lua.data.Function;
import com.jdy.lua.data.Value;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class NativeFunction extends Function {
    private final String funcName;
    public NativeFunction(String funcName, NativeFuncBody funcBody) {
        // 原生方法不会使用到Block
        super(null, funcBody);
        isNative = true;
        this.funcName  = funcName;
    }


    public static class Builder {
        private String funcName;
        private final List<String> parameterNames = new ArrayList<>();
        private java.util.function.Function<List<Value>,Value> execute;
        private boolean hasMultiVar = false;

        public Builder funcName(String funcName) {
            this.funcName = funcName;
            return this;
        }
        public Builder parameterNames(List<String> params) {
            parameterNames.addAll(params);
            return this;
        }
        public Builder parameterNames(String... params) {
            parameterNames.addAll(Arrays.asList(params));
            return this;
        }
        public Builder hasMultiVar(){
            hasMultiVar = true;
            return this;
        }
        public Builder execute(java.util.function.Function<List<Value>,Value> execute) {
            this.execute = execute;
            return this;
        }
        public NativeFunction build(){
            NativeFuncBody body = new NativeFuncBody(parameterNames, hasMultiVar, execute);
            return  new NativeFunction(funcName, body);
        }

    }

    /**
     * 执行函数
     */
    public Value execute(List<Value> args){
        NativeFuncBody body = (NativeFuncBody) getBody();
        return body.getExecute().apply(args);
    }

    public static Builder builder(){
        return new Builder();
    }
}
