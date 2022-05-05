package com.jdy.lua.lcodes2;

public interface Generator {
     GenerateInfo generate(InstructionGenerator generator);
     GenerateInfo generate(InstructionGenerator generator,GenerateInfo info);
}
