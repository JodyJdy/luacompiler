package com.jdy.lua;

import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.vm.FuncInfo;
import com.jdy.lua.vm.InstructionGenerator;
import com.jdy.lua.vm.RuntimeFunc;
import com.jdy.lua.vm.Vm;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;

/**
 * @author jdy
 * @title: Lvm
 * @description:
 * @data 2023/9/20 17:06
 */
public class Lvm {

    /**
     *运行输入的文件
     */
    public static void run(InputStream inputStream) {
        try {
            run(CharStreams.fromStream(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void run(CharStream charStream) {
        LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(charStream)));
        //加载库函数
        NativeLoader.loadLibrary();
        LuaParser.ChunkContext context = luaParser.chunk();
        Statement result = Parser.parseBlock(context.block());
        FuncInfo funcInfo = FuncInfo.createFunc();
        InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
        instructionGenerator.generateStatement(result);
        //填充jmp指针
        FuncInfo.fillJMP();

        long start = System.currentTimeMillis();
        Vm.execute(new RuntimeFunc(funcInfo, null));
        System.out.println(System.currentTimeMillis() - start);
    }

    public static void run(File file) {
        try {
            run(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runFileName(String fileName) {
        try {
            run(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void run(String string) {
        run(CharStreams.fromString(string));
    }
}
