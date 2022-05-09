package com.jdy.lua.lcodes2;

public interface Generator {
      /**
       * 用于expr使用，a, n一般 作为生成指令中的 A,B
       */
     void generate(InstructionGenerator generator, int a, int n);

    /**
     * Statement不需要 参数
     */
    void generate(InstructionGenerator ins);
}
