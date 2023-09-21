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
        while (pc < len) {
            ByteCode code = codes[pc];
            if (code instanceof Jmp jmp) {
                pc = jmp.a;
                continue;
            } else if (code instanceof Calculate calculate) {
                runCalculate(calculate);
            } else if (code instanceof Compare compare) {
                runCompare(compare);
            } else if (code instanceof Test test) {
                StackElement val = registers[test.a];
                //为真继续执行
                if (val.getValue() != NIL && val.getValue() != FALSE) {
                    pc++;
                }
            } else if (code instanceof LoadVar loadvar) {
                registers[loadvar.a].setValue(registers[loadvar.b].getValue());
            } else if (code instanceof LoadUpVar loadupvar) {
                registers[loadupvar.a].setValue(runtimeFunc.upValList.get(loadupvar.b).up.getValue());
            } else if (code instanceof LoadGlobal loadglobal) {
                registers[loadglobal.a].setValue(
                        FuncInfo.getGlobalVal(loadglobal.b).getVal()
                );
            } else if (code instanceof LoadConstant loadconstant) {
                registers[loadconstant.a].setValue(
                        FuncInfo.getConstant(loadconstant.b)
                );
            } else if (code instanceof LoadFunc loadfunc) {
                runLoadFunc(loadfunc);
            } else if (code instanceof SaveNil savenil) {
                if (savenil.a == SaveNil.LOCAL_VAR) {
                    registers[savenil.b].setValue(NIL);
                } else if (savenil.a == SaveNil.UPVAL) {
                    runtimeFunc.upValList.get(savenil.b).up.setValue(NIL);
                } else {
                    FuncInfo.getGlobalVal(savenil.b).setVal(NIL);
                }
            } else if (code instanceof SaveVar savevar) {
                registers[savevar.a].setValue(registers[savevar.b].getValue());
            } else if (code instanceof SaveGlobal saveglobal) {
                FuncInfo.getGlobalVal(saveglobal.a)
                        .setVal(registers[saveglobal.b].getValue());
            } else if (code instanceof SaveUpval saveupval) {
                runtimeFunc.upValList.get(saveupval.a).up
                        .setValue(registers[saveupval.b].getValue());
            } else if (code instanceof Return) {
                break;
            } else if (code instanceof ReturnMulti returnmulti) {
                result = runReturnMulti(returnmulti);
                break;
            } else if (code instanceof NewTable newtable) {
                registers[newtable.a].setValue(new Table());
            } else if (code instanceof GetTable gettable) {
                Table table = Checker.checkTable(registers[gettable.a].getValue());
                Value val = table.get(registers[gettable.b].getValue());
                registers[gettable.a].setValue(val);
            } else if (code instanceof GetTableMethod gettablemethod) {
                Table table = Checker.checkTable(registers[gettablemethod.a].getValue());
                Value val = table.get(registers[gettablemethod.b].getValue());
                //交换值
                registers[gettablemethod.a].setValue(val);
                registers[gettablemethod.b].setValue(table);
            } else if (code instanceof SetTableNil settablenil) {
                Table table = Checker.checkTable(registers[settablenil.a].getValue());
                Value key = table.get(registers[settablenil.b].getValue());
                table.addVal(key, NIL);
            } else if (code instanceof SetTable settable) {
                Table table = Checker.checkTable(registers[settable.a].getValue());
                Value key = registers[settable.b].getValue();
                Value value = registers[settable.c].getValue();
                table.addVal(key, value);
            } else if (code instanceof Call call) {
                runCall(call);
            } else if (code instanceof VarArgs varargs) {
                runVarargs(varargs);
            } else if (code instanceof NumberFor numberfor) {
                NumberValue init = Checker.checkNumber(registers[numberfor.a].getValue());
                NumberValue finalValue = Checker.checkNumber(registers[numberfor.a + 1].getValue());
                NumberValue step = Checker.checkNumber(registers[numberfor.a + 2].getValue());
                if (step.getF() > 0 && init.getF() <= finalValue.getF()) {
                    pc++;
                }
                if (step.getF() <= 0 && init.getF() >= finalValue.getF()) {
                    pc++;
                }

            } else if (code instanceof EndNumberFor endnumberfor) {
                NumberValue init = Checker.checkNumber(registers[endnumberfor.a].getValue());
                NumberValue step = Checker.checkNumber(registers[endnumberfor.a + 2].getValue());
                registers[endnumberfor.a].setValue(new NumberValue(init.getF() + step.getF()));
            } else if (code instanceof Length length) {
                Value val = registers[length.a].getValue();
                if (val instanceof StringValue str) {
                    registers[length.a].setValue(new NumberValue(str.getVal().length()));
                } else if (val instanceof Table table) {
                    registers[length.a].setValue(table.len());
                }
            } else if (code instanceof Not not) {
                Value val = registers[not.a].getValue();
                if (FALSE.equals(val)) {
                    registers[not.a].setValue(TRUE);
                } else {
                    registers[not.a].setValue(FALSE);
                }
            } else if (code instanceof Negative negative) {
                NumberValue val = Checker.checkNumber(registers[negative.a].getValue());
                registers[negative.a].setValue(new NumberValue(val.getF() * -1));
            } else if (code instanceof BitReverse bitreverse) {
                CalculateValue val = Checker.checkCalculate(registers[bitreverse.a].getValue());
                registers[bitreverse.a].setValue(val.unm());
            } else if (code instanceof LoadGlobalModule loadglobalmodule) {
                String moduleName = Checker.checkStringVal(FuncInfo.getConstant(loadglobalmodule.b));
                Table table = NativeLoader.loadVmModule(moduleName);
                FuncInfo.getGlobalVal(loadglobalmodule.a).setVal(table);
            } else if (code instanceof LoadModule loadmodule) {
                String moduleName = Checker.checkStringVal(FuncInfo.getConstant(loadmodule.b));
                Table table = NativeLoader.loadVmModule(moduleName);
                registers[loadmodule.a].setValue(table);
            } else if (code instanceof GenericFor genericfor) {
                //三个一组 s,f,var
                List<Value> resultList = new ArrayList<>();
                for (int i = genericfor.c; i <= genericfor.d; i += 3) {
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
                    for (int i = 0; i < resultList.size() && i + genericfor.a <= genericfor.b; i++) {
                        registers[i + genericfor.a].setValue(resultList.get(i));
                    }
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

    private void runCalculate(Calculate code) {
        StackElement left = registers[code.b];
        StackElement right = registers[code.c];
        Value l = left.getValue();
        Value r = right.getValue();
        if (l instanceof CalculateValue leftVal && r instanceof CalculateValue rightVal) {
            if (code instanceof Add add) {
                registers[add.a].setValue(leftVal.add(rightVal));
            } else if (code instanceof Sub sub) {
                registers[sub.a].setValue(leftVal.sub(rightVal));
            } else if (code instanceof MUL mul) {
                registers[mul.a].setValue(leftVal.mul(rightVal));
            } else if (code instanceof Div div) {
                registers[div.a].setValue(leftVal.div(rightVal));
            } else if (code instanceof Mod mod) {
                registers[mod.a].setValue(leftVal.mod(rightVal));
            } else if (code instanceof IntMod intmod) {
                registers[intmod.a].setValue(leftVal.intMod(rightVal));
            } else if (code instanceof Pow pow) {
                registers[pow.a].setValue(leftVal.pow(rightVal));
            } else if (code instanceof BitAnd bitand) {
                registers[bitand.a].setValue(leftVal.bitAnd(rightVal));
            } else if (code instanceof BitOr bitor) {
                registers[bitor.a].setValue(leftVal.bitOr(rightVal));
            } else if (code instanceof BitLeftShift bitleftmove) {
                registers[bitleftmove.a].setValue(leftVal.bitLeftMove(rightVal));
            } else if (code instanceof BitRightShift bitRightShift) {
                registers[bitRightShift.a].setValue(leftVal.bitOr(rightVal));
            } else if (code instanceof Cat cat) {
                registers[cat.a].setValue(leftVal.concat(rightVal));
            }
        }
        if (code instanceof And and) {
            if (l == FALSE || r == NIL) {
                registers[and.a].setValue(l);
            } else {
                registers[and.a].setValue(r);
            }
        } else if (code instanceof Or or) {
            if (l == TRUE || (l != FALSE && l != NIL)) {
                registers[or.a].setValue(l);
            } else {
                registers[or.a].setValue(r);
            }
        }
    }

    private void runCompare(Compare code) {
        StackElement left = registers[code.a];
        StackElement right = registers[code.b];
        Value leftVal = left.getValue();
        Value rightVal = right.getValue();
        if (code instanceof Eq) {
            registers[code.a].setValue(leftVal.eq(rightVal));
        } else if (code instanceof Ne) {
            registers[code.a].setValue(leftVal.ne(rightVal));
        } else if (code instanceof Ge) {
            registers[code.a].setValue(leftVal.ge(rightVal));
        } else if (code instanceof Gt) {
            registers[code.a].setValue(leftVal.gt(rightVal));
        } else if (code instanceof Lt) {
            registers[code.a].setValue(leftVal.lt(rightVal));
        } else if (code instanceof Le) {
            registers[code.a].setValue(leftVal.le(rightVal));
        }
    }

    private Value runReturnMulti(ReturnMulti code) {
        List<Value> returnList = new ArrayList<>();
        int realB;
        if (code.b == -1) {
            realB = runtimeFunc.used;
        } else {
            realB = code.b;
        }
        for (int i = code.a; i <= realB; i++) {
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
    private void runLoadFunc(LoadFunc loadfunc) {
        FuncInfo loadFuncInfo = FuncInfo.funcInfos().get(loadfunc.b);
        RuntimeFunc funcRuntime = new RuntimeFunc(loadFuncInfo, runtimeFunc);
        registers[loadfunc.a].setValue(
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
