package com.jdy.lua.luanative;

import com.jdy.lua.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jdy.lua.executor.Checker.checkString;
import static com.jdy.lua.executor.Checker.checkNumber;

/**
 * 字符串相关函数
 */
public class LuaString {
    /**
     * table 名为 string
     */
    public static String TABLE_NAME = "string";
    public static Table STRING = new Table();

    static {
        // string.byte(s [, i [, j]])
        STRING.addVal("byte", byteFunc());
        // string.char(...)
        STRING.addVal("char", charFunc());
        // string.dump(func [, strip])
        STRING.addVal("dump", dump());
        // string.find(s, pattern [, init [, plain]])
        STRING.addVal("find", find());
        // string.format(formatstring, ...)
        STRING.addVal("format", format());
        // string.gmatch(s, pattern)
        STRING.addVal("gmatch", gmatch());
        // string.gsub(s, pattern, repl [, n])
        STRING.addVal("gsub", gsub());
        // string.len(s)
        STRING.addVal("len", len());
        // string.lower(s)
        STRING.addVal("lower", lower());
        // string.match(s, pattern [, init])
        STRING.addVal("match", match());
        // string.pack(fmt, v1, v2, ...)
        STRING.addVal("pack", pack());
        // string.packsize(fmt)
        STRING.addVal("packsize", packsize());
        // string.rep(s, n [, sep])
        STRING.addVal("rep", rep());
        // string.reverse(s)
        STRING.addVal("reverse", reverse());
        // string.sub(s, i [, j])
        STRING.addVal("sub", sub());
        // string.unpack(fmt, s [, pos])
        STRING.addVal("unpack", unpack());
        // string.upper(s)
        STRING.addVal("upper", upper());
    }

    // string.byte(s [, i [, j]])
    public static Value byteFunc(List<Value> args) {
        StringValue s = checkString(args.get(0));
        String str = s.getVal();

        int i = 1; // 默认值为1
        if (args.size() >= 2 && args.get(1) != NilValue.NIL) {
            i = checkNumber(args.get(i)).intValue();
        }
        int j = i; // 默认值为i
        if (args.size() >= 3 && args.get(2) != NilValue.NIL) {
            j = checkNumber(args.get(2)).getValue().intValue();
        }

        // 调整索引为0基
        int startIndex = i > 0 ? i - 1 : str.length() + i;
        int endIndex = j > 0 ? j - 1 : str.length() + j;

        // 边界检查
        if (startIndex < 0) startIndex = 0;
        if (endIndex >= str.length()) endIndex = str.length() - 1;

        // 如果起始位置大于结束位置，返回空结果
        if (startIndex > endIndex) {
            return NilValue.NIL;
        }

        List<Value> results = new java.util.ArrayList<>();
        for (int idx = startIndex; idx <= endIndex; idx++) {
            results.add(new NumberValue(str.charAt(idx)));
        }

        if (results.isEmpty()) {
            return NilValue.NIL;
        } else if (results.size() == 1) {
            return results.get(0);
        } else {
            return new MultiValue(results);
        }
    }

    private static NativeJavaFunction byteFunc() {
        return NativeJavaFunction.builder().funcName("byte")
                .parameterNames("s", "i", "j")
                .execute(LuaString::byteFunc).build();
    }

    // string.char(...)
    public static Value charFunc(List<Value> args) {
        StringBuilder sb = new StringBuilder();
        for (Value arg : args) {
            int code = checkNumber(arg).getValue().intValue();
            sb.append((char) code);
        }
        return new StringValue(sb.toString());
    }

    private static NativeJavaFunction charFunc() {
        return NativeJavaFunction.builder().funcName("char")
                .parameterNames("...")
                .hasMultiVar()
                .execute(LuaString::charFunc).build();
    }

    // string.dump(func [, strip])
    public static Value dump(List<Value> args) {
        // 简化实现，实际Lua中这个函数会序列化函数
        // 这里仅作为占位符
        return new StringValue("");
    }

    private static NativeJavaFunction dump() {
        return NativeJavaFunction.builder().funcName("dump")
                .parameterNames("func", "strip")
                .execute(LuaString::dump).build();
    }

    // string.find(s, pattern [, init [, plain]])
    public static Value find(List<Value> args) {
        // 简化实现
        StringValue s = checkString(args.get(0));
        String sVal = s.getVal();
        StringValue pattern = checkString(args.get(1));
        String pVal = pattern.getVal();

        int init = 0;
        if (args.size() >= 3) {
            Value initValue = args.get(2);
            if (initValue.type() == DataTypeEnum.NUMBER) {
                // java下标从0开始
                init = checkNumber(initValue).getValue().intValue() - 1;
            }
        }
        boolean plain = false;
        if (args.size() >= 4) {
            Value plainValue = args.get(3);
            plain =  BoolValue.TRUE.equals(plainValue);
        }

        if (plain) {
            int index =sVal.indexOf(pVal,init);
            if (index != -1) {
                return  MultiValue.of(new NumberValue(index + 1), new NumberValue(index + pVal.length()));
            }
        } else{
            Pattern p = Pattern.compile(pVal);
            Matcher matcher = p.matcher(sVal);
            if (matcher.find(init)) {
                List<Value> result = new ArrayList<>();
                result.add(new NumberValue(matcher.start() + 1));
                result.add(new NumberValue(matcher.end()));
                for(int i = 1; i <= matcher.groupCount(); i++){
                    result.add(new StringValue(matcher.group(i)));
                }
                return MultiValue.of(result);
            }
        }
        return NilValue.NIL;
    }

    private static NativeJavaFunction find() {
        return NativeJavaFunction.builder().funcName("find")
                .parameterNames("s", "pattern", "init", "plain")
                .execute(LuaString::find).build();
    }

// string.format(formatstring, ...)  使用java的String.format方法
    public static Value format(List<Value> args) {
        StringValue formatStr = checkString(args.get(0));
        String format = formatStr.getVal();
        List<Value> formatArgs = args.subList(1, args.size());
        return new StringValue(String.format(format, formatArgs.toArray()));
    }

    private static NativeJavaFunction format() {
        return NativeJavaFunction.builder().funcName("format")
                .parameterNames("formatstring", "...")
                .hasMultiVar()
                .execute(LuaString::format).build();
    }

    // string.gmatch(s, pattern)
    public static Value gmatch(List<Value> args) {
        // 简化实现
        return NilValue.NIL;
    }

    private static NativeJavaFunction gmatch() {
        return NativeJavaFunction.builder().funcName("gmatch")
                .parameterNames("s", "pattern")
                .execute(LuaString::gmatch).build();
    }

    // string.gsub(s, pattern, repl [, n])
    public static Value gsub(List<Value> args) {
        // 简化实现
        return new MultiValue(List.of(
                args.get(0), // 返回原始字符串
                new NumberValue(0) // 匹配次数为0
        ));
    }

    private static NativeJavaFunction gsub() {
        return NativeJavaFunction.builder().funcName("gsub")
                .parameterNames("s", "pattern", "repl", "n")
                .execute(LuaString::gsub).build();
    }

    // string.len(s)
    public static Value len(List<Value> args) {
        StringValue s = checkString(args.get(0));
        return new NumberValue(s.getVal().length());
    }

    private static NativeJavaFunction len() {
        return NativeJavaFunction.builder().funcName("len")
                .parameterNames("s")
                .execute(LuaString::len).build();
    }

    // string.lower(s)
    public static Value lower(List<Value> args) {
        StringValue s = checkString(args.get(0));
        return new StringValue(s.getVal().toLowerCase());
    }

    private static NativeJavaFunction lower() {
        return NativeJavaFunction.builder().funcName("lower")
                .parameterNames("s")
                .execute(LuaString::lower).build();
    }

    // string.match(s, pattern [, init])
    public static Value match(List<Value> args) {
        // 简化实现
        return NilValue.NIL;
    }

    private static NativeJavaFunction match() {
        return NativeJavaFunction.builder().funcName("match")
                .parameterNames("s", "pattern", "init")
                .execute(LuaString::match).build();
    }

    // string.pack(fmt, v1, v2, ...)
    public static Value pack(List<Value> args) {
        // 简化实现
        return new StringValue("");
    }

    private static NativeJavaFunction pack() {
        return NativeJavaFunction.builder().funcName("pack")
                .parameterNames("fmt", "...")
                .execute(LuaString::pack).build();
    }

    // string.packsize(fmt)
    public static Value packsize(List<Value> args) {
        // 简化实现
        return new NumberValue(0);
    }

    private static NativeJavaFunction packsize() {
        return NativeJavaFunction.builder().funcName("packsize")
                .parameterNames("fmt")
                .execute(LuaString::packsize).build();
    }

    // string.rep(s, n [, sep])
    public static Value rep(List<Value> args) {
        StringValue s = checkString(args.get(0));
        int n = checkNumber(args.get(1)).getValue().intValue();
        String sep = "";
        if (args.size() > 2) {
            sep = checkString(args.get(2)).getVal();
        }

        if (n <= 0) {
            return new StringValue("");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0 && !sep.isEmpty()) {
                sb.append(sep);
            }
            sb.append(s.getVal());
        }
        return new StringValue(sb.toString());
    }

    private static NativeJavaFunction rep() {
        return NativeJavaFunction.builder().funcName("rep")
                .parameterNames("s", "n", "sep")
                .execute(LuaString::rep).build();
    }

    // string.reverse(s)
    public static Value reverse(List<Value> args) {
        StringValue s = checkString(args.get(0));
        return new StringValue(new StringBuilder(s.getVal()).reverse().toString());
    }

    private static NativeJavaFunction reverse() {
        return NativeJavaFunction.builder().funcName("reverse")
                .parameterNames("s")
                .execute(LuaString::reverse).build();
    }

    // string.sub(s, i [, j])
    public static Value sub(List<Value> args) {
        StringValue s = checkString(args.get(0));
        String str = s.getVal();
        int len = str.length();

        int i = checkNumber(args.get(1)).getValue().intValue();
        int j = -1; // 默认为-1，即字符串末尾
        if (args.size() >= 3) {
            j = checkNumber(args.get(2)).getValue().intValue();
        } else {
            j = len;
        }

        // 处理负数索引
        if (i < 0) i = len + i + 1;
        if (j < 0) j = len + j + 1;

        // 调整边界
        if (i < 1) i = 1;
        if (j > len) j = len;

        // 如果i > j 或者超出范围，返回空字符串
        if (i > j || i > len) {
            return new StringValue("");
        }

        // 转换为0基索引
        return new StringValue(str.substring(i - 1, j));
    }

    private static NativeJavaFunction sub() {
        return NativeJavaFunction.builder().funcName("sub")
                .parameterNames("s", "i", "j")
                .execute(LuaString::sub).build();
    }

    // string.unpack(fmt, s [, pos])
    public static Value unpack(List<Value> args) {
        // 简化实现
        return new StringValue("");
    }

    private static NativeJavaFunction unpack() {
        return NativeJavaFunction.builder().funcName("unpack")
                .parameterNames("fmt", "s", "pos")
                .execute(LuaString::unpack).build();
    }

    // string.upper(s)
    public static Value upper(List<Value> args) {
        StringValue s = checkString(args.get(0));
        return new StringValue(s.getVal().toUpperCase());
    }

    private static NativeJavaFunction upper() {
        return NativeJavaFunction.builder().funcName("upper")
                .parameterNames("s")
                .execute(LuaString::upper).build();
    }
}
