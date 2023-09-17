import com.jdy.lua.antlr4.LuaLexer;
import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.executor.Executor;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.parser.Parser;
import com.jdy.lua.statement.Statement;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.FileInputStream;

public class Test {
    public static void main(String[] args) throws Exception {
        FileInputStream fils = new FileInputStream("src/test/b.lua");
        LuaParser luaParser = new LuaParser(new BufferedTokenStream(new LuaLexer(CharStreams.fromStream(fils))));
        //加载库函数
        NativeLoader.loadLibrary();
        //设置模块加载地址
        NativeLoader.setModulePath("src/test");
        LuaParser.ChunkContext context = luaParser.chunk();
        Statement result = Parser.parseBlock(context.block());
        new Executor((Statement.BlockStatement) result).execute();
    }

}