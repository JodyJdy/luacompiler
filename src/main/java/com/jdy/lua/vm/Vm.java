package com.jdy.lua.vm;

import com.jdy.lua.data.*;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.luanative.NativeFuncBody;
import com.jdy.lua.luanative.NativeFunction;
import com.jdy.lua.luanative.NativeLoader;

import java.util.ArrayList;
import java.util.List;

import static com.jdy.lua.data.BoolValue.FALSE;
import static com.jdy.lua.data.BoolValue.TRUE;
import static com.jdy.lua.data.NilValue.NIL;
import static com.jdy.lua.vm.ByteCode.*;

/**
 * @author jdy
 * @title: Vm
 * @description:
 * @data 2023/9/19 13:30
 */
public class Vm {

    public Vm(RuntimeFunc runtimeFunc) {
        this.runtimeFunc = runtimeFunc;
        this.registers = runtimeFunc.registers;
    }

    private final RuntimeFunc runtimeFunc;

    private StackElement[] registers;

    /**
     * 从指定的位置开始运行
     */

    public Value executeFrom(int from) {
        //使用数组能提升寻址性能
        ByteCode[] codes = new ByteCode[runtimeFunc.funcInfo.codes.size()];
        runtimeFunc.funcInfo.getCodes().toArray(codes);
        registers = runtimeFunc.registers;
        int len = codes.length;
        Value result = NIL;
        int pc = from;
        // 记录下字节码的值，减少 instanceof 运算
        int a = 0, b = 0, c = 0, d = 0;
        Label:
        while (pc < len) {
            ByteCode code = codes[pc];
            //减少判断
            a = code.getA();
            b = code.getB();
            c = code.getC();
            d = code.getD();


            switch (code.type()) {
                case Jmp -> {
                    pc = a;
                    continue;
                }
                case Add -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.add(r));
                }
                case Sub -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.sub(r));
                }
                case MUL -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.mul(r));
                }
                case Div -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.div(r));
                }
                case Mod -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.mod(r));
                }
                case IntMod -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.intMod(r));
                }
                case Pow -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.pow(r));
                }
                case BitAnd -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.bitAnd(r));
                }
                case BitOr -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.bitOr(r));
                }
                case BitLeftShift -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.bitLeftMove(r));
                }
                case BitRightShift -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.bitRightMove(r));
                }
                case Cat -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    CalculateValue l = (CalculateValue) left.getValue();
                    CalculateValue r = (CalculateValue) right.getValue();
                    registers[a].setValue(l.concat(r));
                }
                case And -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    Value l = left.getValue();
                    Value r = right.getValue();
                    if (l == FALSE || l == NIL) {
                        registers[a].setValue(l);
                    } else {
                        registers[a].setValue(r);
                    }
                }
                case Or -> {
                    StackElement left = registers[b];
                    StackElement right = registers[c];
                    Value l = left.getValue();
                    Value r = right.getValue();
                    if (l == FALSE || r == NIL) {
                        registers[a].setValue(r);
                    } else {
                        registers[a].setValue(l);
                    }

                }
                case Eq -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.eq(rightVal));
                }
                case Ne -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.ne(rightVal));
                }
                case Ge -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.ge(rightVal));
                }
                case Gt -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.gt(rightVal));
                }
                case Lt -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.lt(rightVal));
                }
                case Le -> {
                    StackElement left = registers[a];
                    StackElement right = registers[b];
                    Value leftVal = left.getValue();
                    Value rightVal = right.getValue();
                    registers[a].setValue(leftVal.le(rightVal));
                }
                case Test -> {
                    StackElement val = registers[a];
                    //为真继续执行
                    if (val.getValue() != NIL && val.getValue() != FALSE) {
                        pc++;
                    }

                }
                case LoadVar -> {
                    registers[a].setValue(registers[b].getValue());
                }
                case LoadUpVar -> {
                    registers[a].setValue(runtimeFunc.upValList.get(b).up.getValue());
                }
                case LoadGlobal -> {
                    registers[a].setValue(
                            FuncInfo.getGlobalVal(b).getVal());
                }
                case LoadConstant -> {
                    registers[a].setValue(
                            FuncInfo.getConstant(b));
                }
                case LoadFunc -> {
                    runLoadFunc(a, b);
                }
                case SaveNil -> {
                    if (a == SaveNil.LOCAL_VAR) {
                        registers[b].setValue(NIL);
                    } else if (a == SaveNil.UPVAL) {
                        runtimeFunc.upValList.get(b).up.setValue(NIL);
                    } else {
                        FuncInfo.getGlobalVal(b).setVal(NIL);
                    }
                }
                case SaveVar -> {
                    registers[a].setValue(registers[b].getValue());
                }
                case SaveGlobal -> {
                    FuncInfo.getGlobalVal(a)
                            .setVal(registers[b].getValue());
                }
                case SaveUpval -> {
                    runtimeFunc.upValList.get(a).up
                            .setValue(registers[b].getValue());
                }
                case Return -> {
                    break Label;
                }
                case ReturnMulti -> {
                    result = runReturnMulti(a, b);
                    break Label;
                }
                case NewTable -> {
                    registers[a].setValue(new Table());
                }
                case GetTable -> {
                    Table table = Checker.checkTable(registers[a].getValue());
                    Value val = table.get(registers[b].getValue());
                    registers[a].setValue(val);
                }
                case GetTableMethod -> {
                    Table table = Checker.checkTable(registers[a].getValue());
                    Value val = table.get(registers[b].getValue());
                    //交换值
                    registers[a].setValue(val);
                    registers[b].setValue(table);
                }
                case SetTableNil -> {
                    Table table = Checker.checkTable(registers[a].getValue());
                    Value key = table.get(registers[b].getValue());
                    table.addVal(key, NIL);
                }
                case SetTable -> {
                    Table table = Checker.checkTable(registers[a].getValue());
                    Value key = registers[b].getValue();
                    Value value = registers[c].getValue();
                    table.addVal(key, value);
                }
                case Call -> {
                    runCall((Call) code);
                }
                case VarArgs -> {
                    runVarargs((VarArgs) code);
                }
                case NumberFor -> {
                    NumberValue init = Checker.checkNumber(registers[a].getValue());
                    NumberValue finalValue = Checker.checkNumber(registers[a + 1].getValue());
                    NumberValue step = Checker.checkNumber(registers[a + 2].getValue());
                    if (step.gtZero() && init.le(finalValue) == TRUE) {
                        pc++;
                    }
                    if (step.leZero() && init.ge(finalValue) == TRUE) {
                        pc++;
                    }
                }
                case EndNumberFor -> {
                    NumberValue init = Checker.checkNumber(registers[a].getValue());
                    NumberValue step = Checker.checkNumber(registers[a + 2].getValue());
                    registers[a].setValue(init.add(step));
                }
                case Length -> {
                    Value val = registers[a].getValue();
                    if (val instanceof StringValue str) {
                        registers[a].setValue(new NumberValue(str.getVal().length()));
                    } else if (val instanceof Table table) {
                        registers[a].setValue(table.len());
                    }
                }
                case Not -> {
                    Value val = registers[a].getValue();
                    if (FALSE.equals(val)) {
                        registers[a].setValue(TRUE);
                    } else {
                        registers[a].setValue(FALSE);
                    }
                }
                case Negative -> {
                    NumberValue val = Checker.checkNumber(registers[a].getValue());
                    registers[a].setValue(val.negative());
                }
                case BitReverse -> {
                    CalculateValue val = Checker.checkCalculate(registers[a].getValue());
                    registers[a].setValue(val.unm());
                }
                case LoadGlobalModule -> {
                    String moduleName = Checker.checkStringVal(FuncInfo.getConstant(b));
                    Table table = NativeLoader.loadVmModule(moduleName);
                    FuncInfo.getGlobalVal(a).setVal(table);
                }
                case LoadModule -> {
                    String moduleName = Checker.checkStringVal(FuncInfo.getConstant(b));
                    Table table = NativeLoader.loadVmModule(moduleName);
                    registers[a].setValue(table);
                }
                case GenericFor -> {
                    List<Value> resultList = new ArrayList<>();
                    for (int i = c; i <= d; i += 3) {
                        Value val = registers[i].getValue();
                        List<Value> args = List.of(registers[i + 1].getValue(), registers[i + 2].getValue());
                        Value returnVal;
                        if (val instanceof NativeFunction nativeFunction) {
                            returnVal = nativeFunction.execute(args);
                        } else if (val instanceof RuntimeFunc runtimeFunc1) {
                            returnVal = runtimeFunc1.call(args);
                        } else {
                            throw new RuntimeException("错误的泛型for循环参数");
                        }
                        //结束循环
                        if (returnVal == NIL) {
                            break;
                        }
                        if (returnVal instanceof MultiValue multiValue) {
                            //结束循环
                            if (multiValue.getValueList().get(0) == NIL) {
                                break;
                            }
                            //更新变量的值
                            registers[i + 2].setValue(multiValue.getValueList().get(0));
                            resultList.addAll(multiValue.getValueList());
                        }
                    }
                    //表示可以继续执行
                    if (!resultList.isEmpty()) {
                        pc++;
                        for (int i = 0; i < resultList.size() && i + a <= b; i++) {
                            registers[i + a].setValue(resultList.get(i));
                        }
                    }
                }
                case SetTableArray -> {
                    runSetTableArray((SetTableArray) code);
                }
            }
            pc++;
        }
        return result;
    }

    /**
     * 执行入口的entry
     */
    public Value execute() {
        return executeFrom(0);
    }


    private Value runReturnMulti(int a,int b) {
        List<Value> returnList = new ArrayList<>();
        int realB;
        if (b == -1) {
            realB = runtimeFunc.used;
        } else {
            realB = b;
        }
        for (int i = a; i <= realB; i++) {
            if (i < registers.length) {
                returnList.add(registers[i].getValue());
            } else {
                returnList.add(NIL);
            }
        }
        return new MultiValue(returnList);
    }

    private void runVarargs(VarArgs code) {
        if (!runtimeFunc.funcInfo.hasMultiArg) {
            throw new RuntimeException("函数不存在可变参数");
        }
        //如果有可变参数，最后一个参数就是
        StackElement element = registers[runtimeFunc.finalParamArg];
        MultiValue multiValue = (MultiValue) element.getValue();
        //获取实际的 varargs需要的数量
        int realB;
        if (code.b == -1) {
            realB = multiValue.getValueList().size();
        } else {
            realB = Math.min(code.b, multiValue.getValueList().size());
        }
        //如果有必要，进行扩容
        resetRegisters(code.a + realB - 1);
        for (int i = 0; i < realB; i++) {
            registers[code.a + i].setValue(multiValue.getValueList().get(i));
        }
    }

    private void runSetTableArray(SetTableArray setTableArray){
       Table table = Checker.checkTable(registers[setTableArray.a] .getValue());
       int end = setTableArray.c;
        if (end == -1) {
            end = runtimeFunc.used;
        }
        for (int i = setTableArray.b; i <= end; i++) {
           table.addVal(registers[i].getValue());
        }
    }

    private void runCall(Call call) {
        Value func = registers[call.a].getValue();
        List<Value> args;
        Value returnValue;
        if (func instanceof RuntimeFunc realCall) {
            FuncInfo info = realCall.funcInfo;
            args = prepareArgs(info.isObjMethod, runtimeFunc.used, info.paramNames.size(),
                    info.hasMultiArg, call);
            returnValue = realCall.call(args);
        } else if (func instanceof NativeFunction nativeFunction) {
            NativeFuncBody body = (NativeFuncBody) nativeFunction.getBody();
            args = prepareArgs(false, runtimeFunc.used, body.getParamNames().size(), body.isHasMultiArg(), call);
            returnValue = nativeFunction.execute(args);
        } else {
            throw new RuntimeException("不支持的函数类型");
        }

        List<Value> finalReturnValue = adjustReturnValue(returnValue, call.d);
        //最后一个值存放的寄存器位置
        int endRegisters = call.a + finalReturnValue.size() - 1;
        resetRegisters(endRegisters);
        //将结果放到寄存器中
        for (int i = 0; i < finalReturnValue.size(); i++) {
            registers[call.a + i].setValue(finalReturnValue.get(i));
        }
    }

    /**
     * 准备函数的参数
     */
    private List<Value> prepareArgs(boolean objMethod, int stackTop, int paramSize, boolean hasMultiArg, Call call) {
        List<Value> args = new ArrayList<>();
        // 第一个参数的位置
        int startArg = call.b;
        // 最后一个参数的位置
        int endArg;
        //call.c == -1 表示栈顶所有的都被处理
        if (call.c == -1) {
            endArg = stackTop;
        } else {
            endArg = call.c;
        }
        int i = 0;
        //调用对象方法，对象自身
        if (objMethod) {
            args.add(registers[i + startArg].getValue());
            i++;
        }
        for (; i < paramSize; i++) {
            if (i + startArg <= endArg) {
                args.add(registers[i + startArg].getValue());
            } else {
                args.add(NIL);
            }
        }
        //全都放到 ...参数中去
        if (hasMultiArg) {
            while (i + startArg <= endArg) {
                args.add(registers[i + startArg].getValue());
                i++;
            }
        }
        return args;
    }

    /**
     * 将返回值处理成 需要的数量
     */
    private List<Value> adjustReturnValue(Value returnValue, int resultNum) {
        if (resultNum == -1) {
            if (returnValue instanceof MultiValue multiValue) {
                resultNum = multiValue.getValueList().size();
            } else {
                resultNum = 1;
            }
        }
        List<Value> values = new ArrayList<>();
        if (returnValue instanceof MultiValue multiValue) {
            List<Value> multiValueValueList = multiValue.getValueList();
            if (multiValueValueList.size() >= resultNum) {
                values.addAll(multiValueValueList.subList(0, resultNum));
            } else {
                values.addAll(multiValueValueList);
            }
        } else {
            values.add(returnValue);
        }
        //不足的补 nil
        while (values.size() < resultNum) {
            values.add(NIL);
        }
        return values;
    }


    /**
     * 此函数是 核心， 通过对LOADFUNC 进行处理， 将函数转换成运行时函数
     */
    private void runLoadFunc(int a,int b) {
        FuncInfo loadFuncInfo = FuncInfo.funcInfos().get(b);
        RuntimeFunc funcRuntime = new RuntimeFunc(loadFuncInfo, runtimeFunc);
        registers[a].setValue(
                funcRuntime);
    }


    public void resetRegisters(int n) {
        runtimeFunc.resetRegister(n);
        //引用可能改变
        registers = runtimeFunc.registers;
    }

    public StackElement[] getRegisters() {
        return registers;
    }
}
