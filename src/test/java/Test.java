import com.jdy.lua.Lua;
import com.jdy.lua.luanative.NativeLoader;

public class Test {
    public static void main(String[] args) throws Exception {
        //设置模块加载地址
        NativeLoader.setModulePath("src/test");
//        Lua.runFileName("src/test/b.lua");

        Lua.commandLine();
    }

}