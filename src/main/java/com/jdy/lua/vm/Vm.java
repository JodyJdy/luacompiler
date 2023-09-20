package com.jdy.lua.vm;

import com.jdy.lua.data.*;
import com.jdy.lua.executor.Checker;
import com.jdy.lua.luanative.NativeFuncBody;
import com.jdy.lua.luanative.NativeFunction;

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
    public static Value execute(FuncInfo entry) {
        List<ByteCode> codeList = entry.getCodes();
        List<StackElement> registers = entry.getRegisters();
        Value result = NIL;
        int pc = 0;
        int startStack = entry.getUsed();
        while (pc < codeList.size()) {
            ByteCode code = codeList.get(pc);

            if (code instanceof JMP jmp) {
                pc = jmp.a;
                continue;
            }
            if (code instanceof Calculate calculate) {
                runCalculate(calculate, registers);
            }
            if (code instanceof Compare compare) {
                runCompare(compare, registers);
            }
            if (code instanceof TEST test) {
                StackElement val = registers.get(test.a);
                //为真继续执行
                if (val.getValue() != NIL && val.getValue() != FALSE) {
                    pc++;
                }
            }

            if (code instanceof LOADVAR loadvar) {
                registers.get(loadvar.a).setValue(registers.get(loadvar.b).getValue());
            }
            if (code instanceof LOADUPVAR loadupvar) {
                registers.get(loadupvar.a).setValue(entry.getUpVal().get(loadupvar.b).getUp().getValue());
            }
            if (code instanceof LOADGLOBAL loadglobal) {
                registers.get(loadglobal.a).setValue(
                        FuncInfo.getGlobalVal(loadglobal.b).getVal()
                );
            }
            if (code instanceof LOADCONSTANT loadconstant) {
                registers.get(loadconstant.a).setValue(
                        FuncInfo.getConstant(loadconstant.b)
                );
            }
            if (code instanceof LOADFUNC loadfunc) {
                registers.get(loadfunc.a).setValue(
                        FuncInfo.funcInfos().get(loadfunc.b)
                );
            }
            if (code instanceof SAVENIL savenil) {
                if (savenil.a == SAVENIL.LOCAL_VAR) {
                    registers.get(savenil.b).setValue(NIL);
                } else if (savenil.a == SAVENIL.UPVAL) {
                    entry.getUpVal().get(savenil.b).getUp().setValue(NIL);
                } else {
                    FuncInfo.getGlobalVal(savenil.b).setVal(NIL);
                }
            }
            if (code instanceof SAVEVAR savevar) {
                registers.get(savevar.a).setValue(registers.get(savevar.b).getValue());
            }
            if (code instanceof SAVEGLOBAL saveglobal) {
                FuncInfo.getGlobalVal(saveglobal.a)
                        .setVal(registers.get(saveglobal.b).getValue());
            }
            if (code instanceof SAVEUPVAL saveupval) {
                entry.getUpVal().get(saveupval.a).getUp()
                        .setValue(registers.get(saveupval.b).getValue());
            }
            if (code instanceof RETURN) {
                break;
            }
            if (code instanceof RETURNMULTI returnmulti) {
                result = runReturnMulti(returnmulti, entry, registers);
                break;
            }
            if (code instanceof NEWTABLE newtable) {
                registers.get(newtable.a).setValue(new Table());
            }
            if (code instanceof GETTABLE gettable) {
                Table table = Checker.checkTable(registers.get(gettable.a).getValue());
                Value val = table.get(registers.get(gettable.b).getValue());
                registers.get(gettable.a).setValue(val);
            }
            if (code instanceof GETTABLEMETHOD gettablemethod) {
                Table table = Checker.checkTable(registers.get(gettablemethod.a).getValue());
                Value val = table.get(registers.get(gettablemethod.b).getValue());
                //交换值
                registers.get(gettablemethod.a).setValue(val);
                registers.get(gettablemethod.b).setValue(table);
            }
            if (code instanceof SETTABLENIL settablenil) {
                Table table = Checker.checkTable(registers.get(settablenil.a).getValue());
                Value key = table.get(registers.get(settablenil.b).getValue());
                table.addVal(key, NIL);
            }
            if (code instanceof SETTABLE settable) {
                Table table = Checker.checkTable(registers.get(settable.a).getValue());
                Value key = registers.get(settable.b).getValue();
                Value value = registers.get(settable.c).getValue();
                table.addVal(key, value);
            }
            if (code instanceof CALL call) {
                runCall(call, entry, registers);
            }
            if (code instanceof VARARGS varargs) {
                runVarargs(varargs, entry, registers);
            }
            pc++;
        }
        //还原堆栈
        entry.resetRegister(startStack);
        return result;
    }

    private static void runCalculate(Calculate code, List<StackElement> registers) {
        StackElement left = registers.get(code.b);
        StackElement right = registers.get(code.c);
        Value l = left.getValue();
        Value r = right.getValue();
        if (l instanceof CalculateValue leftVal && r instanceof CalculateValue rightVal) {
            if (code instanceof ADD add) {
                registers.get(add.a).setValue(leftVal.add(rightVal));
            }
            if (code instanceof SUB sub) {
                registers.get(sub.a).setValue(leftVal.sub(rightVal));
            }
            if (code instanceof MUL mul) {
                registers.get(mul.a).setValue(leftVal.mul(rightVal));
            }
            if (code instanceof DIV div) {
                registers.get(div.a).setValue(leftVal.div(rightVal));
            }
            if (code instanceof MOD mod) {
                registers.get(mod.a).setValue(leftVal.mod(rightVal));
            }
            if (code instanceof INTMOD intmod) {
                registers.get(intmod.a).setValue(leftVal.intMod(rightVal));
            }
            if (code instanceof POW pow) {
                registers.get(pow.a).setValue(leftVal.pow(rightVal));
            }
            if (code instanceof BITAND bitand) {
                registers.get(bitand.a).setValue(leftVal.bitAnd(rightVal));
            }
            if (code instanceof BITOR bitor) {
                registers.get(bitor.a).setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof BITLEFTMOVE bitleftmove) {
                registers.get(bitleftmove.a).setValue(leftVal.bitLeftMove(rightVal));
            }
            if (code instanceof BITRIGHTMOVE bitrightmove) {
                registers.get(bitrightmove.a).setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof CAT cat) {
                registers.get(cat.a).setValue(leftVal.concat(rightVal));
            }
        }
        if (code instanceof AND and) {
            if (l == FALSE || r == NIL) {
                registers.get(and.a).setValue(l);
            } else {
                registers.get(and.a).setValue(r);
            }
        }
        if (code instanceof OR or) {
            if (l == TRUE || (l != FALSE && l != NIL)) {
                registers.get(or.a).setValue(l);
            } else {
                registers.get(or.a).setValue(r);
            }
        }
    }

    private static void runCompare(Compare code, List<StackElement> registers) {
        StackElement left = registers.get(code.a);
        StackElement right = registers.get(code.b);
        Value leftVal = left.getValue();
        Value rightVal = right.getValue();
        if (code instanceof EQ) {
            registers.get(code.a).setValue(leftVal.eq(rightVal));
        }
        if (code instanceof NE) {
            registers.get(code.a).setValue(leftVal.ne(rightVal));
        }
        if (code instanceof GE) {
            registers.get(code.a).setValue(leftVal.ge(rightVal));
        }
        if (code instanceof GT) {
            registers.get(code.a).setValue(leftVal.gt(rightVal));
        }
        if (code instanceof LT) {
            registers.get(code.a).setValue(leftVal.lt(rightVal));
        }
        if (code instanceof LE) {
            registers.get(code.a).setValue(leftVal.le(rightVal));
        }
    }

    private static Value runReturnMulti(RETURNMULTI code, FuncInfo entry, List<StackElement> registers) {
        List<Value> returnList = new ArrayList<>();
        int realB;
        if (code.b == -1) {
            realB = entry.getUsed();
        } else {
            realB = code.b;
        }
        for (int i = code.a; i <= realB; i++) {
            returnList.add(registers.get(i).getValue());
        }
        return new MultiValue(returnList);
    }

    private static void runVarargs(VARARGS code, FuncInfo entry, List<StackElement> registers) {
        StackElement element = entry.searchVar("...");
        MultiValue multiValue = (MultiValue) element.getValue();
        //获取实际的 varargs需要的数量
        int realB;
        if (code.b == -1) {
            realB = multiValue.getValueList().size();
        } else {
            realB = Math.min(code.b,multiValue.getValueList().size());
        }
        //如果有必要，进行扩容
        entry.allocRegister2N(code.a + realB - 1);
        for (int i = 0; i < realB; i++) {
            registers.get(code.a + i).setValue(multiValue.getValueList().get(i));
        }
    }

    private static void runCall(CALL call, FuncInfo entry, List<StackElement> registers) {
        Value func = registers.get(call.a).getValue();
        List<Value> args;
        Value returnValue;
        if (func instanceof FuncInfo funcInfo) {
            args = prepareArgs(entry.getUsed(),funcInfo.getParamNames().size(), funcInfo.hasMultiArg, call, registers);
            returnValue = funcInfo.call(args);
        } else if (func instanceof NativeFunction nativeFunction) {
            NativeFuncBody body = (NativeFuncBody) nativeFunction.getBody();
            args = prepareArgs(entry.getUsed(),body.getParamNames().size(), body.isHasMultiArg(), call, registers);
            returnValue = nativeFunction.execute(args);
        } else {
            throw new RuntimeException("不支持的函数类型");
        }

        List<Value> finalReturnValue = adjustReturnValue(returnValue, call.d);
        //最后一个值存放的寄存器位置
        int endRegisters = call.a + finalReturnValue.size() - 1;
        //可能不够，扩容
        entry.allocRegister2N(endRegisters);
        //可能多了，裁剪寄存器数量
        entry.resetRegister(endRegisters);
        //将结果放到寄存器中
        for (int i = 0; i < finalReturnValue.size(); i++) {
            registers.get(call.a + i).setValue(finalReturnValue.get(i));
        }
    }

    /**
     * 准备函数的参数
     */
    private static List<Value> prepareArgs(int stackTop,int paramSize, boolean hasMultiArg, CALL call, List<StackElement> registers) {
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
        for (; i < paramSize; i++) {
            if (i + startArg <= endArg) {
                args.add(registers.get(i + startArg).getValue());
            } else {
                args.add(NIL);
            }
        }
        //全都放到 ...参数中去
        if (hasMultiArg) {
            while (i + startArg <= endArg) {
                args.add(registers.get(i + startArg).getValue());
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

}
