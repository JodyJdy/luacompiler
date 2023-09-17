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

    static String modulePath;
    public static Table loadModule(String moduleName) {
        if (modulePath == null) {
            throw new RuntimeException("未配置模块地址");
        }
        String path = modulePath + File.separator + moduleName+".lua";
        try {
            FileInputStream moduleFile = new FileInputStream(path);
            LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromStream(moduleFile))));
            LuaParser.ChunkContext context = luaParser.chunk();
            Statement result = Parser.parseBlock(context.block());
            return Checker.checkTable(new Executor((Statement.BlockStatement) result).execute());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("模块找不到"+moduleName);
        } catch (IOException e) {
            throw new RuntimeException("模块读取失败");
        }
    }
}
