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

    /**
     * 执行入口的entry
     */
    public static Value execute(RuntimeFunc runtimeFunc) {
        //使用数组能提升寻址性能
        ByteCode[] codes = new ByteCode[runtimeFunc.funcInfo.codes.size()];
        runtimeFunc.funcInfo.getCodes().toArray(codes);
        int len = codes.length;
        Value result = NIL;
        int pc = 0;
        while (pc < len) {
            ByteCode code = codes[pc];
            if (code instanceof JMP jmp) {
                pc = jmp.a;
                continue;
            } else if (code instanceof Calculate calculate) {
                runCalculate(calculate, runtimeFunc.registers);
            } else if (code instanceof Compare compare) {
                runCompare(compare, runtimeFunc.registers);
            } else if (code instanceof TEST test) {
                StackElement val = runtimeFunc.registers[test.a];
                //为真继续执行
                if (val.getValue() != NIL && val.getValue() != FALSE) {
                    pc++;
                }
            } else if (code instanceof LOADVAR loadvar) {
                runtimeFunc.registers[loadvar.a].setValue(runtimeFunc.registers[loadvar.b].getValue());
            } else if (code instanceof LOADUPVAR loadupvar) {
                runtimeFunc.registers[loadupvar.a].setValue(runtimeFunc.upValList.get(loadupvar.b).up.getValue());
            } else if (code instanceof LOADGLOBAL loadglobal) {
                runtimeFunc.registers[loadglobal.a].setValue(
                        FuncInfo.getGlobalVal(loadglobal.b).getVal()
                );
            } else if (code instanceof LOADCONSTANT loadconstant) {
                runtimeFunc.registers[loadconstant.a].setValue(
                        FuncInfo.getConstant(loadconstant.b)
                );
            } else if (code instanceof LOADFUNC loadfunc) {
                runLoadFunc(loadfunc, runtimeFunc);
            } else if (code instanceof SAVENIL savenil) {
                if (savenil.a == SAVENIL.LOCAL_VAR) {
                    runtimeFunc.registers[savenil.b].setValue(NIL);
                } else if (savenil.a == SAVENIL.UPVAL) {
                    runtimeFunc.upValList.get(savenil.b).up.setValue(NIL);
                } else {
                    FuncInfo.getGlobalVal(savenil.b).setVal(NIL);
                }
            } else if (code instanceof SAVEVAR savevar) {
                runtimeFunc.registers[savevar.a].setValue(runtimeFunc.registers[savevar.b].getValue());
            } else if (code instanceof SAVEGLOBAL saveglobal) {
                FuncInfo.getGlobalVal(saveglobal.a)
                        .setVal(runtimeFunc.registers[saveglobal.b].getValue());
            } else if (code instanceof SAVEUPVAL saveupval) {
                runtimeFunc.upValList.get(saveupval.a).up
                        .setValue(runtimeFunc.registers[saveupval.b].getValue());
            } else if (code instanceof RETURN) {
                break;
            } else if (code instanceof RETURNMULTI returnmulti) {
                result = runReturnMulti(returnmulti, runtimeFunc, runtimeFunc.registers);
                break;
            } else if (code instanceof NEWTABLE newtable) {
                runtimeFunc.registers[newtable.a].setValue(new Table());
            } else if (code instanceof GETTABLE gettable) {
                Table table = Checker.checkTable(runtimeFunc.registers[gettable.a].getValue());
                Value val = table.get(runtimeFunc.registers[gettable.b].getValue());
                runtimeFunc.registers[gettable.a].setValue(val);
            } else if (code instanceof GETTABLEMETHOD gettablemethod) {
                Table table = Checker.checkTable(runtimeFunc.registers[gettablemethod.a].getValue());
                Value val = table.get(runtimeFunc.registers[gettablemethod.b].getValue());
                //交换值
                runtimeFunc.registers[gettablemethod.a].setValue(val);
                runtimeFunc.registers[gettablemethod.b].setValue(table);
            } else if (code instanceof SETTABLENIL settablenil) {
                Table table = Checker.checkTable(runtimeFunc.registers[settablenil.a].getValue());
                Value key = table.get(runtimeFunc.registers[settablenil.b].getValue());
                table.addVal(key, NIL);
            } else if (code instanceof SETTABLE settable) {
                Table table = Checker.checkTable(runtimeFunc.registers[settable.a].getValue());
                Value key = runtimeFunc.registers[settable.b].getValue();
                Value value = runtimeFunc.registers[settable.c].getValue();
                table.addVal(key, value);
            } else if (code instanceof CALL call) {
                runCall(call, runtimeFunc, runtimeFunc.registers);
            } else if (code instanceof VARARGS varargs) {
                runVarargs(varargs, runtimeFunc, runtimeFunc.registers);
            } else if (code instanceof NUMBERFOR numberfor) {
                NumberValue init = Checker.checkNumber(runtimeFunc.registers[numberfor.a].getValue());
                NumberValue finalValue = Checker.checkNumber(runtimeFunc.registers[numberfor.a + 1].getValue());
                NumberValue step = Checker.checkNumber(runtimeFunc.registers[numberfor.a + 2].getValue());
                if (step.getF() > 0 && init.getF() <= finalValue.getF()) {
                    pc++;
                }
                if (step.getF() <= 0 && init.getF() >= finalValue.getF()) {
                    pc++;
                }

            } else if (code instanceof ENDNUMBERFOR endnumberfor) {
                NumberValue init = Checker.checkNumber(runtimeFunc.registers[endnumberfor.a].getValue());
                NumberValue step = Checker.checkNumber(runtimeFunc.registers[endnumberfor.a + 2].getValue());
                runtimeFunc.registers[endnumberfor.a].setValue(new NumberValue(init.getF() + step.getF()));
            } else if (code instanceof LENGTH length) {
                Value val = runtimeFunc.registers[length.a].getValue();
                if (val instanceof StringValue str) {
                    runtimeFunc.registers[length.a].setValue(new NumberValue(str.getVal().length()));
                } else if (val instanceof Table table) {
                    runtimeFunc.registers[length.a].setValue(table.len());
                }
            } else if (code instanceof NOT not) {
                Value val = runtimeFunc.registers[not.a].getValue();
                if (FALSE.equals(val)) {
                    runtimeFunc.registers[not.a].setValue(TRUE);
                } else {
                    runtimeFunc.registers[not.a].setValue(FALSE);
                }
            } else if (code instanceof SINGLESUB singlesub) {
                NumberValue val = Checker.checkNumber(runtimeFunc.registers[singlesub.a].getValue());
                runtimeFunc.registers[singlesub.a].setValue(new NumberValue(val.getF() * -1));
            } else if (code instanceof BITREVERSE bitreverse) {
                CalculateValue val = Checker.checkCalculate(runtimeFunc.registers[bitreverse.a].getValue());
                runtimeFunc.registers[bitreverse.a].setValue(val.unm());
            } else if (code instanceof LOADGLOBALMODULE loadglobalmodule) {
                String moduleName = Checker.checkStringVal(FuncInfo.getConstant(loadglobalmodule.b));
                Table table = NativeLoader.loadVmModule(moduleName);
                FuncInfo.getGlobalVal(loadglobalmodule.a).setVal(table);
            } else if (code instanceof LOADMODULE loadmodule) {
                String moduleName = Checker.checkStringVal(FuncInfo.getConstant(loadmodule.b));
                Table table = NativeLoader.loadVmModule(moduleName);
                runtimeFunc.registers[loadmodule.a].setValue(table);
            } else if (code instanceof GENERICFOR genericfor) {
                //三个一组 s,f,var
                List<Value> resultList = new ArrayList<>();
                for (int i = genericfor.c; i <= genericfor.d; i += 3) {
                    Value val = runtimeFunc.registers[i].getValue();
                    List<Value> args = List.of(runtimeFunc.registers[i + 1].getValue(), runtimeFunc.registers[i + 2].getValue());
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
                        runtimeFunc.registers[i + 2].setValue(multiValue.getValueList().get(0));
                        resultList.addAll(multiValue.getValueList());
                    }
                }
                //表示可以继续执行
                if (!resultList.isEmpty()) {
                    pc++;
                    for (int i = 0; i < resultList.size() && i + genericfor.a <= genericfor.b; i++) {
                        runtimeFunc.registers[i + genericfor.a].setValue(resultList.get(i));
                    }
                }
            }
            pc++;
        }
        return result;
    }

    private static void runCalculate(Calculate code, StackElement[] registers) {
        StackElement left = registers[code.b];
        StackElement right = registers[code.c];
        Value l = left.getValue();
        Value r = right.getValue();
        if (l instanceof CalculateValue leftVal && r instanceof CalculateValue rightVal) {
            if (code instanceof ADD add) {
                registers[add.a].setValue(leftVal.add(rightVal));
            }
            if (code instanceof SUB sub) {
                registers[sub.a].setValue(leftVal.sub(rightVal));
            }
            if (code instanceof MUL mul) {
                registers[mul.a].setValue(leftVal.mul(rightVal));
            }
            if (code instanceof DIV div) {
                registers[div.a].setValue(leftVal.div(rightVal));
            }
            if (code instanceof MOD mod) {
                registers[mod.a].setValue(leftVal.mod(rightVal));
            }
            if (code instanceof INTMOD intmod) {
                registers[intmod.a].setValue(leftVal.intMod(rightVal));
            }
            if (code instanceof POW pow) {
                registers[pow.a].setValue(leftVal.pow(rightVal));
            }
            if (code instanceof BITAND bitand) {
                registers[bitand.a].setValue(leftVal.bitAnd(rightVal));
            }
            if (code instanceof BITOR bitor) {
                registers[bitor.a].setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof BITLEFTMOVE bitleftmove) {
                registers[bitleftmove.a].setValue(leftVal.bitLeftMove(rightVal));
            }
            if (code instanceof BITRIGHTMOVE bitrightmove) {
                registers[bitrightmove.a].setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof CAT cat) {
                registers[cat.a].setValue(leftVal.concat(rightVal));
            }
        }
        if (code instanceof AND and) {
            if (l == FALSE || r == NIL) {
                registers[and.a].setValue(l);
            } else {
                registers[and.a].setValue(r);
            }
        }
        if (code instanceof OR or) {
            if (l == TRUE || (l != FALSE && l != NIL)) {
                registers[or.a].setValue(l);
            } else {
                registers[or.a].setValue(r);
            }
        }
    }

    private static void runCompare(Compare code, StackElement[] registers) {
        StackElement left = registers[code.a];
        StackElement right = registers[code.b];
        Value leftVal = left.getValue();
        Value rightVal = right.getValue();
        if (code instanceof EQ) {
            registers[code.a].setValue(leftVal.eq(rightVal));
        }
        if (code instanceof NE) {
            registers[code.a].setValue(leftVal.ne(rightVal));
        }
        if (code instanceof GE) {
            registers[code.a].setValue(leftVal.ge(rightVal));
        }
        if (code instanceof GT) {
            registers[code.a].setValue(leftVal.gt(rightVal));
        }
        if (code instanceof LT) {
            registers[code.a].setValue(leftVal.lt(rightVal));
        }
        if (code instanceof LE) {
            registers[code.a].setValue(leftVal.le(rightVal));
        }
    }

    private static Value runReturnMulti(RETURNMULTI code, RuntimeFunc runtimeFunc, StackElement[] registers) {
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

    private static void runVarargs(VARARGS code, RuntimeFunc runtimeFunc, StackElement[] registers) {
        if (!runtimeFunc.funcInfo.hasMultiArg) {
            throw new RuntimeException("函数不存在可变参数");
        }
        //如果有可变参数，最后一个参数就是
        StackElement element = runtimeFunc.registers[runtimeFunc.finalParamArg];
        MultiValue multiValue = (MultiValue) element.getValue();
        //获取实际的 varargs需要的数量
        int realB;
        if (code.b == -1) {
            realB = multiValue.getValueList().size();
        } else {
            realB = Math.min(code.b, multiValue.getValueList().size());
        }
        //如果有必要，进行扩容
        runtimeFunc.resetRegister(code.a + realB - 1);
        for (int i = 0; i < realB; i++) {
            registers[code.a + i].setValue(multiValue.getValueList().get(i));
        }
    }

    private static void runCall(CALL call, RuntimeFunc runtimeFunc, StackElement[] registers) {
        Value func = registers[call.a].getValue();
        List<Value> args;
        Value returnValue;
        if (func instanceof RuntimeFunc realCall) {
            FuncInfo info = realCall.funcInfo;
            args = prepareArgs(info.isObjMethod, runtimeFunc.used, info.paramNames.size(),
                    info.hasMultiArg, call, registers);
            returnValue = realCall.call(args);
        } else if (func instanceof NativeFunction nativeFunction) {
            NativeFuncBody body = (NativeFuncBody) nativeFunction.getBody();
            args = prepareArgs(false, runtimeFunc.used, body.getParamNames().size(), body.isHasMultiArg(), call, registers);
            returnValue = nativeFunction.execute(args);
        } else {
            throw new RuntimeException("不支持的函数类型");
        }

        List<Value> finalReturnValue = adjustReturnValue(returnValue, call.d);
        //最后一个值存放的寄存器位置
        int endRegisters = call.a + finalReturnValue.size() - 1;
        runtimeFunc.resetRegister(endRegisters);
        //将结果放到寄存器中
        for (int i = 0; i < finalReturnValue.size(); i++) {
            registers[ call.a + i ].setValue(finalReturnValue.get(i));
        }
    }

    /**
     * 准备函数的参数
     */
    private static List<Value> prepareArgs(boolean objMethod, int stackTop, int paramSize, boolean hasMultiArg, CALL call, StackElement[] registers) {
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
    private static List<Value> adjustReturnValue(Value returnValue, int resultNum) {
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
    private static void runLoadFunc(LOADFUNC loadfunc, RuntimeFunc runtimeFunc) {
        FuncInfo loadFuncInfo = FuncInfo.funcInfos().get(loadfunc.b);
        RuntimeFunc funcRuntime = new RuntimeFunc(loadFuncInfo, runtimeFunc);
        runtimeFunc.registers[loadfunc.a].setValue(
                funcRuntime);
    }

}
