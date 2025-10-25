支持完整的lua语法特性,但没有完整实现lua中的库函数

##  lua解释器实现

直接使用java执行lua代码

```java

import com.jdy.lua.Lua;

public class Test {
    public static void main(String[] args)  {
        // 执行文件
        Lua.runFileName("src/test/b.lua");
        //执行字符串形式的脚本
        Lua.run("print('helo')");
        //启动交互式执行
        Lua.commandLine();
    }
}

```

## lua编译器实现

编译成自定义的字节码后，执行字节码

```java

import com.jdy.lua.Lvm;

public class Test {
    public static void main(String[] args)  {
        // 执行文件
        Lvm.runFileName("src/test/b.lua");
        //执行字符串形式的脚本
        Lvm.run("print('helo')");
        //启动交互式执行
        Lvm.commandLine();
    }
}


```

## 实现的库

* global
* math




