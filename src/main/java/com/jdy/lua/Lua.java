package com.jdy.lua;

import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.util.Scanner;

/**
 * @author jdy
 * @title: Lua
 * @description:
 * @data 2023/9/18 9:20
 */
public class Lua {

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
        long start = System.currentTimeMillis();
        new Executor((Statement.BlockStatement) result).execute();
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

    /**
     * 交互式 命令行
     */
    public static void commandLine(){
        //加载库函数
        NativeLoader.loadLibrary();
        //创建一个空的 BlockStatement,用于启动一个Executor
        Statement.BlockStatement blockStatement = new Statement.BlockStatement();
        Executor executor = new Executor(blockStatement);
        //启动
        executor.execute();
        Scanner scanner = new Scanner(System.in);
        System.out.println("----------  lua  -----------------输入 bye  结束执行");
        System.out.print(">>");
        while (scanner.hasNext()) {
            //读取一行命令
            String line = scanner.nextLine();
            if ("bye".equals(line)) {
                break;
            }
            LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromString(line))));
            LuaParser.CommandLineContext context = luaParser.commandLine();
            if (context.exp() != null) {
                Expr expr = Parser.parseExpr(context.exp());
                System.out.println(expr.visitExpr(executor));
            } else if (context.stat() != null) {
                Statement statement = Parser.parseStat(context.stat());
                statement.visitStatement(executor);
            }
            System.out.print(">>");
        }

    }
}
