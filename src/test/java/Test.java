import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;
import com.jdy.lua.statement.Expr;
import jdk.jshell.spi.ExecutionControl;

import java.awt.*;

public class Test {
    public static void main(String[] args) throws Exception {
        //虚拟机执行文件
        Lvm.runFileName("src/test/b.lua");
        //ast执行文件
        Lua.runFileName("src/test/b.lua");
        System.out.println("-----");
    }

}