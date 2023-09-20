import com.jdy.lua.Lua;
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
import org.antlr.v4.runtime.CharStreams;

public class Test {
    public static void main(String[] args) throws Exception {
//        Lua.runFileName("src/test/iter.lua");
        LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromFileName("src/test/b.lua"))));
        //加载库函数
        NativeLoader.loadLibrary();
        LuaParser.ChunkContext context = luaParser.chunk();
        Statement.BlockStatement result = (Statement.BlockStatement) Parser.parseBlock(context.block());
        FuncInfo funcInfo = FuncInfo.createFunc();
        InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
        instructionGenerator.generateStatement(result);
        FuncInfo.fillJMP();


        Vm.execute(new RuntimeFunc(funcInfo,null));

        System.out.println("---------------执行结束--------------");

        FuncInfo.funcInfos().forEach(FuncInfo::showDebug);
        FuncInfo.showGlobal();



    }

}