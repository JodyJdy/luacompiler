It supports the complete Lua syntax features, but does not fully implement the library functions in Lua.

<p align="center">
  <a href="./readme.md">English</a> 
  <a href="./readme_CN.md">简体中文</a> 
</p>


##  Lua interpreter

Execute Lua code directly using Java.

```java

import com.jdy.lua.Lua;

public class Test {
    public static void main(String[] args)  {
        // execute file
        Lua.runFileName("src/test/b.lua");
        // execute lua  script string
        Lua.run("print('helo')");
        //Interactive execution
        Lua.commandLine();
    }
}

```

## lua compiler

After compiling into custom bytecode, execute the bytecode.

```java

import com.jdy.lua.Lvm;

public class Test {
    public static void main(String[] args)  {
        // execute file
        Lvm.runFileName("src/test/b.lua");
        // execute lua  script string
        Lvm.run("print('helo')");
        // Interactive execution
        Lvm.commandLine();
    }
}


```

## Config Module Search Path

```java

import com.jdy.lua.luanative.NativeLoader;
public class Test {
    public static void main(String[] args) {
        // windows
        NativeLoader.setModulePath("src/test;src/test/lua");
        // linux
        NativeLoader.setModulePath("src/test:src/test/lua");
        
        // Lvm.runFileName("src/test/b.lua");
        // Lua.runFileName("src/test/b.lua");
    }
}

## The content to be achieved

* global
* math
* string

### The function interfaces implemented in the global library

- `assert()`
- `collectgarbage()`
- `error()`
- `pcall()`
- `rawequal()`
- `rawget()`
- `rawlen()`
- `rawset()`
- `type()`
- `print()`
- `println()`
- `setmetatable()`
- `getmetatable()`
- `next()`

### The function interfaces implemented in the math library

- `math.abs(x)` - 计算绝对值
- `math.acos(x)` - 计算反余弦值
- `math.add(a, b)` - 加法运算
- `math.asin(x)` - 计算反正弦值
- `math.atan(y[, x])` - 计算反正切值
- `math.ceil(x)` - 向上取整
- `math.cos(x)` - 计算余弦值
- `math.deg(x)` - 弧度转换为角度
- `math.exp(x)` - 计算指数值
- `math.floor(x)` - 向下取整
- `math.fmod(x, y)` - 计算模数
- `math.huge` - 正无穷大常量
- `math.log(x[, base])` - 计算对数值
- `math.max(...)` - 求最大值
- `math.min(...)` - 求最小值
- `math.modf(x)` - 分解整数和小数部分
- `math.pi` - 圆周率常量
- `math.rad(x)` - 角度转换为弧度
- `math.random([m[, n]])` - 生成随机数
- `math.randomseed(x)` - 设置随机数种子
- `math.sin(x)` - 计算正弦值
- `math.sqrt(x)` - 计算平方根
- `math.tan(x)` - 计算正切值
- `math.tointeger(x)` - 转换为整数
- `math.type(x)` - 获取数字类型
- `math.ult(m, n)` - 无符号长整型比较
- `math.maxinteger` - 最大长整型值
- `math.mininteger` - 最小长整型值

### The function interfaces implemented in the string library

- `string.byte(s [, i [, j]])` - 返回字符的内部数值代码
- `string.char(...)` - 根据数值代码创建字符
- `string.find(s, pattern [, init [, plain]])` - 查找模式匹配
- `string.format(formatstring, ...)` - 格式化字符串（基础实现）
- `string.len(s)` - 返回字符串长度
- `string.lower(s)` - 转换为小写
- `string.reverse(s)` - 反转字符串
- `string.gmatch(s, pattern [, init])` 使用java正则格式

