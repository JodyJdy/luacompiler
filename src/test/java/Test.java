import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.vm.FuncInfo;
import com.jdy.lua.vm.GlobalVal;
import com.jdy.lua.vm.InstructionGenerator;
import com.jdy.lua.vm.Vm;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

public class Test {
    public static void main(String[] args) throws Exception {
        LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromFileName("src/test/b.lua"))));
        //加载库函数
        NativeLoader.loadLibrary();
        LuaParser.ChunkContext context = luaParser.chunk();
        Statement.BlockStatement result = (Statement.BlockStatement) Parser.parseBlock(context.block());
        FuncInfo funcInfo = FuncInfo.createFunc();
        InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
        instructionGenerator.generateStatement(result);
        FuncInfo.funcInfos().forEach(FuncInfo::showDebug);

        Vm.execute(funcInfo);

        FuncInfo.showGlobal();



    }

}