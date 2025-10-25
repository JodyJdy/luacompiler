import com.jdy.lua.Lua;
import com.jdy.lua.Lvm;
import com.jdy.lua.luanative.NativeLoader;
import com.jdy.lua.statement.Expr;
import jdk.jshell.spi.ExecutionControl;

import java.awt.*;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println("lua 执行:");
        Lvm.runFileName("src/test/b.lua");
//        System.out.println("lua 执行:");
        Lua.runFileName("src/test/b.lua");
    }

}