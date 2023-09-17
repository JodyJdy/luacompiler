package com.jdy.lua.luanative;

import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.data.Table;
import com.jdy.lua.executor.Block;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
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
        Block.addNative(Math.TABLE_NAME,Math.MATH);
        Block.addNative("print",GlobalMethod.print());
        Block.addNative("println",GlobalMethod.println());
        Block.addNative("type",GlobalMethod.type());
        Block.addNative("setmetatable",GlobalMethod.setmetatable());
        Block.addNative("getmetatable",GlobalMethod.getmetatable());
        Block.addNative("ipairs",GenericFor.ipairs());
        Block.addNative("pairs",GenericFor.pairs());
    }
    public static void setModulePath(String modulePath) {
        NativeLoader.modulePath = modulePath;
    }


    /**
     * 正在加载的模块
     */
    private static final Set<String> loadingModule = Collections.synchronizedSet(new HashSet<>());

    private static String modulePath;
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
}
