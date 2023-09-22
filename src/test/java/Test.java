import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;

public class Test {
    public static void main(String[] args) throws Exception {
        //虚拟机执行文件
        Lvm.runFileName("src/test/iter.lua");
        //ast执行文件
        Lua.runFileName("src/test/iter.lua");

    }

}