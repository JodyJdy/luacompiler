package com.jdy.lua.lcodes2;

import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lparser.*;

import java.util.List;

import static com.jdy.lua.LuaConstants.*;
import static com.jdy.lua.LuaConstants.LUA_TSTRING;
import static com.jdy.lua.lopcodes.Instructions.*;
import static com.jdy.lua.lopcodes.OpCode.*;

import static com.jdy.lua.lcodes.LCodes.*;
import static com.jdy.lua.lparser.ExpKind.*;

public class Lcodes {

    /**
     *  对于常量的处理
     */
    private static int addK(FuncState fs, TValue key, TValue value){
        TValue val = new TValue();
        Proto proto = fs.getProto();
        LexState lex = fs.getLexState();
        TValue idx = lex.getH().get(key);
        if(idx != null){
            return (int) idx.getI();
        }
        //创建一个新的常量到常量数组里面
        int k = proto.getConstants().size();
        val.setI(k);
        //将值 和 下标，存储
        lex.getH().put(value,val);
        //存储到常量数组里面去
        proto.addConstants(value);
        return k;
    }

    private static int stringConstant(FuncState fs, String t){
        TValue v = TValue.strValue(t);
        return addK(fs,v,v);
    }

    public static Instruction getInstruction(FuncState f, GenerateInfo expDesc){
        Proto proto = f.getProto();
        return proto.getInstruction(expDesc.getInfo());
    }

    /**
     * 修正 返回一个结果的表达式；
     * 如果表达式不是一个多返回值的结果，就无需处理
     */
    public static void luaK_setOneRet(FuncState fs, GenerateInfo e){
        if(e.getKind() == VCALL){
            //函数调用会变成 VNONRELOC,表示函数调用的结果需要存放在固定的寄存器里面
            e.setKind(VNONRELOC);
            e.setInfo(getArgA(getInstruction(fs,e)));
        } else if(e.getKind()== VVARARG){
            setArgC(getInstruction(fs,e),2);
            //表达式的结果可以存放在任意的寄存器里
            e.setKind(VRELOC);
        }
    }

    /**
     * 根据 常量表达式 获取 常量
     */
    public static TValue const2Val(FuncState fs,GenerateInfo desc){
        DynData dynData = fs.getLexState().getDyd();
        List<Vardesc> vardescList = dynData.getArr();
        if(desc.getKind() == ExpKind.VCONST){
            return vardescList.get(desc.getInfo()).getK();
        }
        return new TValue();
    }

    /**
     * 常量转成 GenerateInfo
     */
    public static void const2Exp(TValue v,GenerateInfo expDesc){
        switch (v.getValueType()) {
            case LUA_TNUMINT:
                expDesc.setKind(VKINT);
                expDesc.setIVal(v.getI());
                break;
            case LUA_TNUMFLONT:
                expDesc.setKind(VKFLT);
                expDesc.setFloatVal(v.getF());
                break;
            case LUA_TFALSE:
                expDesc.setKind(VFALSE);
                break;
            case LUA_TTRUE:
                expDesc.setKind(VTRUE);
                break;
            case LUA_TNIL:
                expDesc.setKind(VNIL);
                break;
            case LUA_TSTRING:
                expDesc.setKind(VKSTR);
                expDesc.setStr((String)v.getObj());
                break;
            default: break;
        }
    }


    public static void luaK_dischargeVars(FuncState fs,GenerateInfo e){
        switch (e.getKind()){
            case VCONST: {
                const2Exp(const2Val(fs, e), e);
                break;
            }
            /**
             * local 变量已经存放在寄存器里了
             */
            case VLOCAL: {
                e.setInfo(e.getRegisterIndex());
                e.setKind(VNONRELOC);
                break;
            }
            /**
             * VRELOC 类型的表示还没有放在确切的寄存器里面，
             * 使用 info 存储指令的位置，再后面 填回去
             */
            case VUPVAL: {
                e.setInfo(luaK_codeABC(fs, OP_GETUPVAL, 0,e.getInfo(), 0));
                e.setKind(VRELOC);
                break;
            }
            case VINDEXUP: {
                e.setInfo(luaK_codeABC(fs, OP_GETTABUP, 0, e.getTable(), e.getIndexForTable()));
                e.setKind(VRELOC);
                break;
            }
            case VINDEXI: {
                //释放寄存器，因为寄存器里面的值，已经包含在下面生成的指令里面了,下面的freeReg同理
                freeReg(fs, e.getTable());
                e.setInfo(luaK_codeABC(fs, OP_GETI, 0, e.getTable(), e.getIndexForTable()));
                e.setKind(VRELOC);
                break;
            }
            case VINDEXSTR: {
                freeReg(fs, e.getTable());
                e.setInfo(luaK_codeABC(fs, OP_GETFIELD, 0, e.getTable(), e.getIndexForTable()));
                e.setKind(VRELOC);
                break;
            }
            case VINDEXED: {
                freeRegs(fs,e.getTable(),e.getIndexForTable());
                e.setInfo(luaK_codeABC(fs, OP_GETTABLE, 0, e.getTable(),e.getIndexForTable()));
                e.setKind(VRELOC);
                break;
            }
            case VVARARG: case VCALL: {
                luaK_setOneRet(fs,e);
                break;
            }
            default: break;
        }

    }

    /**
     * 将表达式存储到寄存器 reg 里面去
     */
    public static void discharge2Reg(FuncState fs, GenerateInfo info, int reg){
        //初步对表达式进行调整
        luaK_dischargeVars(fs,info);
        switch (info.getKind()) {
            case VNIL: {
                luaK_Nil(fs, reg, 1);
                break;
            }
            case VFALSE: {
                luaK_codeABC(fs, OP_LOADFALSE, reg, 0, 0);
                break;
            }
            case VTRUE: {
                luaK_codeABC(fs, OP_LOADTRUE, reg, 0, 0);
                break;
            }
            case VKSTR: {
                info.setKind(VK);
                info.setInfo(stringConstant(fs,info.getStr()));
                //继续执行
            }
            case VK: {
                luaK_codeK(fs, reg,info.getInfo());
                break;
            }
            case VKFLT: {
                luaK_Float(fs, reg,info.getFloatVal());
                break;
            }
            case VKINT: {
                luaK_int(fs, reg, info.getIVal());
                break;
            }
            case VRELOC: {
                // 将参数 A 设置为 寄存器 reg的值
                Instruction pc = getInstruction(fs, info);
                setArgA(pc, reg);
                break;
            }
            case VNONRELOC: {
                //通过move指令，将值赋值给指定的寄存器
                if (reg != info.getInfo())
                    luaK_codeABC(fs, OP_MOVE, reg, info.getInfo(), 0);
                break;
            }
            default: {
                return;  /* nothing to do... */
            }
        }
        info.setInfo(reg);
        info.setKind(VNONRELOC);
    }
}
