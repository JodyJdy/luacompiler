package com.jdy.lua;

import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.data.Value;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.vm.FuncInfo;
import com.jdy.lua.vm.InstructionGenerator;
import com.jdy.lua.vm.RuntimeFunc;
import com.jdy.lua.vm.Vm;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.*;
import java.util.Scanner;

/**
 * @author jdy
 * @title: Lvm
 * @description:
 * @data 2023/9/20 17:06
 */
public class Lvm {

    /**
     * 运行输入的文件
     */
    public static Value run(InputStream inputStream) {
        try {
            return run(CharStreams.fromStream(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value run(CharStream charStream) {
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
        return new Vm(new RuntimeFunc(funcInfo, null)).execute();
    }

    public static Value run(File file) {
        try {
            return run(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value runFileName(String fileName) {
        try {
            return run(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value run(String string) {
        return run(CharStreams.fromString(string));
    }


    /**
     * 交互式 命令行
     */
    public static void commandLine() {
        //加载库函数
        NativeLoader.loadLibrary();
        FuncInfo funcInfo = FuncInfo.createFunc();
        Vm vm = new Vm(new RuntimeFunc(funcInfo, null));
        Scanner scanner = new Scanner(System.in);
        System.out.println("----------  lua  -----------------输入 bye  结束执行, 多行使用\\结尾");
        System.out.print(">>");
        int runFrom = 0;
        StringBuilder command = new StringBuilder();
        while (scanner.hasNext()) {
            //读取一行命令
            String line = scanner.nextLine();
            if ("bye".equals(line)) {
                break;
            }
            //多行
            if(line.charAt(line.length()-1)=='\\'){
                line = line.substring(0,line.length()-1);
                command.append(line);
                continue;
            } else{
                command.append(line);
            }
            LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromString(command.toString()))));
            LuaParser.CommandLineContext context = luaParser.commandLine();
            InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
            if (context.exp() != null) {
                Expr expr = Parser.parseExpr(context.exp());
                int reg = instructionGenerator.generateExpr(expr, 1);
                vm.resetRegisters(funcInfo.getRegisters().size());
                FuncInfo.fillJMP();
                vm.executeFrom(runFrom);
                //打印寄存器里面的内容
                System.out.println(vm.getRegisters()[reg].getValue());
            } else if (context.stat() != null) {
                Statement statement = Parser.parseStat(context.stat());
                instructionGenerator.generateStatement(statement);
                vm.resetRegisters(funcInfo.getRegisters().size());
                FuncInfo.fillJMP();
                vm.executeFrom(runFrom);
            }
            //调整下次执行的位置
            runFrom = funcInfo.getCodes().size();
            command = new StringBuilder();
        }
        System.out.print(">>");
    }
}
