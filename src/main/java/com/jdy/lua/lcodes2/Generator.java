package com.jdy.lua.lcodes2;

public interface Generator {
      /**
       * a, n一般 作为生成指令中的 A,B
       */
     void generate(InstructionGenerator generator, int a, int n);
}
