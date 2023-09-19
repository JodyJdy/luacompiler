package com.jdy.lua.vm;

import com.jdy.lua.data.CalculateValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.Value;

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
        int pc = 0;
        while (pc < codeList.size()) {
            ByteCode code = codeList.get(pc);

            if (code instanceof JMP jmp) {
                pc =  jmp.a;
                continue;
            }
            if (code instanceof ADD add) {
                StackElement left = registers.get(add.b);
                StackElement right = registers.get(add.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(add.a).setValue(leftVal.add(rightVal));
            }
            if (code instanceof SUB sub) {
                StackElement left = registers.get(sub.b);
                StackElement right = registers.get(sub.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(sub.a).setValue(leftVal.sub(rightVal));
            }
            if (code instanceof MUL mul) {
                StackElement left = registers.get(mul.b);
                StackElement right = registers.get(mul.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(mul.a).setValue(leftVal.mul(rightVal));
            }
            if (code instanceof DIV div) {
                StackElement left = registers.get(div.b);
                StackElement right = registers.get(div.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(div.a).setValue(leftVal.div(rightVal));
            }
            if (code instanceof MOD mod) {
                StackElement left = registers.get(mod.b);
                StackElement right = registers.get(mod.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(mod.a).setValue(leftVal.mod(rightVal));
            }
            if (code instanceof INTMOD intmod) {
                StackElement left = registers.get(intmod.b);
                StackElement right = registers.get(intmod.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(intmod.a).setValue(leftVal.intMod(rightVal));
            }
            if (code instanceof POW pow) {
                StackElement left = registers.get(pow.b);
                StackElement right = registers.get(pow.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(pow.a).setValue(leftVal.pow(rightVal));
            }
            if (code instanceof BITAND bitand) {
                StackElement left = registers.get(bitand.b);
                StackElement right = registers.get(bitand.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(bitand.a).setValue(leftVal.bitAnd(rightVal));
            }
            if (code instanceof BITOR bitor) {
                StackElement left = registers.get(bitor.b);
                StackElement right = registers.get(bitor.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(bitor.a).setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof BITLEFTMOVE bitleftmove) {
                StackElement left = registers.get(bitleftmove.b);
                StackElement right = registers.get(bitleftmove.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(bitleftmove.a).setValue(leftVal.bitLeftMove(rightVal));
            }
            if (code instanceof BITRIGHTMOVE bitrightmove) {
                StackElement left = registers.get(bitrightmove.b);
                StackElement right = registers.get(bitrightmove.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(bitrightmove.a).setValue(leftVal.bitOr(rightVal));
            }
            if (code instanceof CAT cat) {
                StackElement left = registers.get(cat.b);
                StackElement right = registers.get(cat.c);
                CalculateValue leftVal = (CalculateValue) left.getValue();
                CalculateValue rightVal = (CalculateValue) right.getValue();
                registers.get(cat.a).setValue(leftVal.concat(rightVal));
            }
            if (code instanceof AND and) {
                StackElement left = registers.get(and.b);
                StackElement right = registers.get(and.c);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                if (leftVal == FALSE || rightVal == NIL) {
                    registers.get(and.a).setValue(leftVal);
                } else{
                    registers.get(and.a).setValue(rightVal);
                }
            }
            if (code instanceof OR or) {
                StackElement left = registers.get(or.b);
                StackElement right = registers.get(or.c);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                if (leftVal == TRUE || (leftVal != FALSE && leftVal != NIL)) {
                    registers.get(or.a).setValue(leftVal);
                } else{
                    registers.get(or.a).setValue(rightVal);
                }
            }
            if (code instanceof TEST test) {
                StackElement val = registers.get(test.a);
                //为真继续执行
                if (val.getValue() != NIL && val.getValue() != FALSE) {
                    pc++;
                }
            }
            if (code instanceof EQ eq) {
                StackElement left = registers.get(eq.a);
                StackElement right = registers.get(eq.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(eq.a).setValue(leftVal.eq(rightVal));
            }
            if (code instanceof NE ne) {
                StackElement left = registers.get(ne.a);
                StackElement right = registers.get(ne.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(ne.a).setValue(leftVal.ne(rightVal));
            }

            if (code instanceof GE ge) {
                StackElement left = registers.get(ge.a);
                StackElement right = registers.get(ge.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(ge.a).setValue(leftVal.ge(rightVal));
            }

            if (code instanceof GT gt) {
                StackElement left = registers.get(gt.a);
                StackElement right = registers.get(gt.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(gt.a).setValue(leftVal.gt(rightVal));
            }

            if (code instanceof LT lt) {
                StackElement left = registers.get(lt.a);
                StackElement right = registers.get(lt.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(lt.a).setValue(leftVal.lt(rightVal));
            }
            if (code instanceof LE le) {
                StackElement left = registers.get(le.a);
                StackElement right = registers.get(le.b);
                Value leftVal = left.getValue();
                Value rightVal = right.getValue();
                registers.get(le.a).setValue(leftVal.le(rightVal));
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
                if (savenil.a == 0) {
                    registers.get(savenil.b).setValue(NIL);
                } else if (savenil.a == 1) {
                    entry.getUpVal().get(savenil.b).getUp().setValue(NIL);
                }else{
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

            pc++;
        }
        return NilValue.NIL;
    }



}
