import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;
import com.jdy.lua.luanative.NativeLoader;

import java.io.IOException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {

//        int[] a = new int[]{1, 2, 3, 4, 5};
//        int[] b = a;
//        a = Arrays.copyOf(a, a.length + 5);
//        System.out.println(Arrays.toString(a));
//        System.out.println(Arrays.toString(b));
 run();
    }
    public static void run() throws IOException {
        NativeLoader.setModulePath("src/test");
        Lvm.runFileName("src/test/b.lua");
        Lua.runFileName("src/test/b.lua");
//        FuncInfo.funcInfos().forEach(FuncInfo::showDebug);
//        FuncInfo.showGlobal();
    }
    public static void run2(){
    }

}