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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NativeLoader {
    /**
     * 加载需要的方法
     */
    public static void loadLibrary() {
        //为 ast 调用添加 native方法
        Block.addNative(Math.TABLE_NAME, Math.MATH);
        Block.addNative(LuaString.TABLE_NAME, LuaString.STRING);
        Block.addNative("print", GlobalMethod.print());
        Block.addNative("println", GlobalMethod.println());
        Block.addNative("type", GlobalMethod.type());
        Block.addNative("setmetatable", GlobalMethod.setmetatable());
        Block.addNative("getmetatable", GlobalMethod.getmetatable());
        Block.addNative("ipairs", GenericFor.ipairs());
        Block.addNative("pairs", GenericFor.pairs());

        Block.addNative("assert", GlobalMethod.assertFunc());
        Block.addNative("collectgarbage", GlobalMethod.collectgarbage());
        Block.addNative("error", GlobalMethod.error());
        Block.addNative("pcall", GlobalMethod.pcall());
        Block.addNative("rawequal", GlobalMethod.rawequal());
        Block.addNative("rawget", GlobalMethod.rawget());
        Block.addNative("rawlen", GlobalMethod.rawlen());
        Block.addNative("rawset", GlobalMethod.rawset());
        Block.addNative("next", GlobalMethod.next());


        //为 vm 调用添加native方法

        FuncInfo.addGlobalVal(Math.TABLE_NAME, Math.MATH);
        FuncInfo.addGlobalVal(LuaString.TABLE_NAME, LuaString.STRING);
        FuncInfo.addGlobalVal("print", GlobalMethod.print());
        FuncInfo.addGlobalVal("println", GlobalMethod.println());
        FuncInfo.addGlobalVal("type", GlobalMethod.type());
        FuncInfo.addGlobalVal("setmetatable", GlobalMethod.setmetatable());
        FuncInfo.addGlobalVal("getmetatable", GlobalMethod.getmetatable());
        FuncInfo.addGlobalVal("ipairs", GenericFor.ipairs());
        FuncInfo.addGlobalVal("pairs", GenericFor.pairs());

        FuncInfo.addGlobalVal("assert", GlobalMethod.assertFunc());
        FuncInfo.addGlobalVal("collectgarbage", GlobalMethod.collectgarbage());
        FuncInfo.addGlobalVal("error", GlobalMethod.error());
        FuncInfo.addGlobalVal("pcall", GlobalMethod.pcall());
        FuncInfo.addGlobalVal("rawequal", GlobalMethod.rawequal());
        FuncInfo.addGlobalVal("rawget", GlobalMethod.rawget());
        FuncInfo.addGlobalVal("rawlen", GlobalMethod.rawlen());
        FuncInfo.addGlobalVal("rawset", GlobalMethod.rawset());
        FuncInfo.addGlobalVal("next", GlobalMethod.next());

    }

    public static void setModulePath(String modulePath) {
        if (modulePath != null && !modulePath.isEmpty()) {
            NativeLoader.modulePath = modulePath;
            NativeLoader.paths = modulePath.split(File.pathSeparator);
        }
    }


    /**
     * 正在加载的模块
     */
    private static final Set<String> loadingModule = Collections.synchronizedSet(new HashSet<>());
    /**
     * 加载完成的模块
     */
    private static final Map<String, Table> loadedModule = new ConcurrentHashMap<>();

    /**
     * 模块搜索路径
     * 在linux平台用 : 分割
     * 在windows平台用 ; 分割
     */
    private static String modulePath;
    /**
     * modulePath 进行切割
     */
    private static String[] paths;


    public static Table loadModule(String moduleName) {
        if (loadedModule.containsKey(moduleName)) {
            return loadedModule.get(moduleName);
        }
        synchronized (NativeLoader.class) {
            if (!loadedModule.containsKey(moduleName)) {
                //模块循环依赖的判断
                if (loadingModule.contains(moduleName)) {
                    throw new RuntimeException(String.format("模块:%s 循环出现了循环依赖", moduleName));
                }
                //设置当前加载的模块
                loadingModule.add(moduleName);
                Table module = doLoadModule(moduleName);
                loadedModule.put(moduleName, module);
                loadingModule.remove(moduleName);
                return module;
            }
        }
        return loadedModule.getOrDefault(moduleName, new Table());
    }

    /**
     * 加载ast执行需要的模块
     */
    public static Table doLoadModule(String moduleName) {
        Statement result = parseModuleStatement(moduleName);
        return Checker.checkTable(new Executor((Statement.BlockStatement) result).execute());
    }

    public static Table loadVmModule(String moduleName) {
        if (loadedModule.containsKey(moduleName)) {
            return loadedModule.get(moduleName);
        }
        synchronized (NativeLoader.class) {
            if (!loadedModule.containsKey(moduleName)) {
                if (loadingModule.contains(moduleName)) {
                    throw new RuntimeException(String.format("模块:%s 循环出现了循环依赖", moduleName));
                }
                loadingModule.add(moduleName);
                Table module = doLoadVmModule(moduleName);
                loadedModule.put(moduleName, module);
                loadingModule.remove(moduleName);
                return module;
            }
        }
        return loadedModule.getOrDefault(moduleName, new Table());
    }


    /**
     * 加载 vm 运行需要的模块
     *
     * @param moduleName 模块名称   a.b.c
     * @return 返回模块对应的 Table 对象
     */
    private static Table doLoadVmModule(String moduleName) {
        Statement result = parseModuleStatement(moduleName);
        FuncInfo funcInfo = FuncInfo.createFunc();
        InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
        instructionGenerator.generateStatement(result);
        //填充jmp指针
        FuncInfo.fillJMP();
        Value val = new Vm(new RuntimeFunc(funcInfo, null)).execute();
        if (val instanceof NilValue) {
            throw new RuntimeException(String.format("模块:%s读取失败", moduleName));
        } else if (val instanceof MultiValue mul) {
            return Checker.checkTable(mul.getValueList().get(0));
        }
        return new Table();
    }

    /**
     * 获取模块文件输入流
     *
     * @param moduleName 模块文件名称
     * @return
     */
    private static FileInputStream getModuleFileInputStream(String moduleName) {
        if (paths == null) {
            throw new RuntimeException("未配置模块地址");
        }
        String moduleFilePath = moduleName.replace('.', File.separatorChar) + ".lua";
        for (String path : paths) {
            String fullPath = path + File.separator + moduleFilePath;
            if (new File(fullPath).exists()) {
                try {
                    return new FileInputStream(fullPath);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException(String.format("模块:%s找不到,搜索路径:%s", moduleName, modulePath));
    }

    /**
     *解析模块文件，返回模块的Statement
     */
    private static  Statement parseModuleStatement(String moduleName) {
        FileInputStream moduleFile = getModuleFileInputStream(moduleName);
        LuaParser luaParser;
        try {
            luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromStream(moduleFile))));
        } catch (IOException e) {
            throw new RuntimeException(String.format("模块:%s 文件读取失败", moduleName));
        }
        LuaParser.ChunkContext context = luaParser.chunk();
        return Parser.parseBlock(context.block());
    }
}
