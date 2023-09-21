import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;
import com.jdy.lua.luanative.NativeLoader;

import java.io.IOException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
        run();
            Lvm.commandLine();

    }
    public static void run() throws IOException {
        NativeLoader.setModulePath("src/test");
        Lvm.runFileName("src/test/b.lua");
//        FuncInfo.funcInfos().forEach(FuncInfo::showDebug);
//        FuncInfo.showGlobal();
    }
    public static void run2(){
    }

}