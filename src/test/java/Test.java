import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;
import com.jdy.lua.statement.Expr;
import jdk.jshell.spi.ExecutionControl;

public class Test {
    public static void main(String[] args) throws Exception {
        //虚拟机执行文件
        long start = System.currentTimeMillis();
        Lvm.runFileName("src/test/b.lua");
        System.out.println(System.currentTimeMillis() - start);
        //ast执行文件
        Lua.runFileName("src/test/b.lua");

    }

}