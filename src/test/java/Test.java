import com.jdy.lua.Lua;
import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.data.Value;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.vm.FuncInfo;
import com.jdy.lua.vm.InstructionGenerator;
import com.jdy.lua.vm.RuntimeFunc;
import com.jdy.lua.vm.Vm;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws Exception {
 run();
    }
    public static void run() throws IOException {
        NativeLoader.setModulePath("src/test");
        LuaParser luaParser;
        try {
            luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromFileName("src/test/b.lua"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //加载库函数
        NativeLoader.loadLibrary();
        LuaParser.ChunkContext context = luaParser.chunk();
        Statement.BlockStatement result = (Statement.BlockStatement) Parser.parseBlock(context.block());
        FuncInfo funcInfo = FuncInfo.createFunc();
        InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
        instructionGenerator.generateStatement(result);
        FuncInfo.fillJMP();

        long start = System.currentTimeMillis();
        Value val = Vm.execute(new RuntimeFunc(funcInfo,null));
        System.out.println(System.currentTimeMillis() - start);
        System.out.println("---------------执行结束--------------");

        FuncInfo.funcInfos().forEach(FuncInfo::showDebug);
        FuncInfo.showGlobal();
    }
    public static void run2(){
      Lua.runFileName("src/test/b.lua");
    }

}