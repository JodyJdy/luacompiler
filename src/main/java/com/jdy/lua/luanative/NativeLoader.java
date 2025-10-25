package com.jdy.lua.luanative;

import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.data.MultiValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Table;
import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Block;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.vm.FuncInfo;
import com.jdy.lua.vm.InstructionGenerator;
import com.jdy.lua.vm.RuntimeFunc;
import com.jdy.lua.vm.Vm;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NativeLoader {
    /**
     * 加载需要的方法
     */
    public static void loadLibrary(){
        //为 ast 调用添加 native方法
        Block.addNative(Math.TABLE_NAME,Math.MATH);
        Block.addNative("print",GlobalMethod.print());
        Block.addNative("println",GlobalMethod.println());
        Block.addNative("type",GlobalMethod.type());
        Block.addNative("setmetatable",GlobalMethod.setmetatable());
        Block.addNative("getmetatable",GlobalMethod.getmetatable());
        Block.addNative("ipairs",GenericFor.ipairs());
        Block.addNative("pairs",GenericFor.pairs());

        Block.addNative("assert",GlobalMethod.assertFunc());
        Block.addNative("collectgarbage",GlobalMethod.collectgarbage());
        Block.addNative("error",GlobalMethod.error());
        Block.addNative("pcall",GlobalMethod.pcall());
        Block.addNative("rawequal",GlobalMethod.rawequal());
        Block.addNative("rawget",GlobalMethod.rawget());
        Block.addNative("rawlen",GlobalMethod.rawlen());
        Block.addNative("rawset",GlobalMethod.rawset());
        Block.addNative("next",GlobalMethod.next());


        //为 vm 调用添加native方法

        FuncInfo.addGlobalVal(Math.TABLE_NAME,Math.MATH);
        FuncInfo.addGlobalVal("print",GlobalMethod.print());
        FuncInfo.addGlobalVal("println",GlobalMethod.println());
        FuncInfo.addGlobalVal("type",GlobalMethod.type());
        FuncInfo.addGlobalVal("setmetatable",GlobalMethod.setmetatable());
        FuncInfo.addGlobalVal("getmetatable",GlobalMethod.getmetatable());
        FuncInfo.addGlobalVal("ipairs",GenericFor.ipairs());
        FuncInfo.addGlobalVal("pairs",GenericFor.pairs());

        FuncInfo.addGlobalVal("assert",GlobalMethod.assertFunc());
        FuncInfo.addGlobalVal("collectgarbage",GlobalMethod.collectgarbage());
        FuncInfo.addGlobalVal("error",GlobalMethod.error());
        FuncInfo.addGlobalVal("pcall",GlobalMethod.pcall());
        FuncInfo.addGlobalVal("rawequal",GlobalMethod.rawequal());
        FuncInfo.addGlobalVal("rawget",GlobalMethod.rawget());
        FuncInfo.addGlobalVal("rawlen",GlobalMethod.rawlen());
        FuncInfo.addGlobalVal("rawset",GlobalMethod.rawset());
        FuncInfo.addGlobalVal("next", GlobalMethod.next());

    }
    public static void setModulePath(String modulePath) {
        NativeLoader.modulePath = modulePath;
    }


    /**
     * 正在加载的模块
     */
    private static final Set<String> loadingModule = Collections.synchronizedSet(new HashSet<>());

    private static String modulePath;

    /**
     *加载ast执行需要的模块
     */
    public static Table loadModule(String moduleName) {
        if (modulePath == null) {
            throw new RuntimeException("未配置模块地址");
        }
        String path = modulePath + File.separator + moduleName+".lua";
        if (loadingModule.contains(path)) {
            throw new RuntimeException(String.format("模块:%s 循环出现了循环依赖", path));
        }
        try {
            loadingModule.add(path);
            FileInputStream moduleFile = new FileInputStream(path);
            LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromStream(moduleFile))));
            LuaParser.ChunkContext context = luaParser.chunk();
            Statement result = Parser.parseBlock(context.block());
            Table module = Checker.checkTable(new Executor((Statement.BlockStatement) result).execute());
            loadingModule.remove(path);
            return module;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("模块:%s找不到",moduleName));
        } catch (IOException e) {
            throw new RuntimeException(String.format("模块:%s读取失败",moduleName));
        }
    }

    /**
     * 加载 vm 运行需要的模块
     * @param moduleName
     * @return
     */
    public static Table loadVmModule(String moduleName) {
        if (modulePath == null) {
            throw new RuntimeException("未配置模块地址");
        }
        String path = modulePath + File.separator + moduleName+".lua";
        if (loadingModule.contains(path)) {
            throw new RuntimeException(String.format("模块:%s 循环出现了循环依赖", path));
        }
        try {
            loadingModule.add(path);
            FileInputStream moduleFile = new FileInputStream(path);
            LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromStream(moduleFile))));
            LuaParser.ChunkContext context = luaParser.chunk();
            Statement result = Parser.parseBlock(context.block());
            FuncInfo funcInfo = FuncInfo.createFunc();
            InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
            instructionGenerator.generateStatement(result);
            Value val = new Vm(new RuntimeFunc(funcInfo,null)).execute();
            if (val instanceof NilValue) {
                throw new RuntimeException(String.format("模块:%s读取失败", moduleName));
            } else if (val instanceof MultiValue mul) {
                return Checker.checkTable(mul.getValueList().get(0));
            }
            loadingModule.remove(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("模块:%s找不到",moduleName));
        } catch (IOException e) {
            throw new RuntimeException(String.format("模块:%s读取失败",moduleName));
        }
        return new Table();
    }
}
