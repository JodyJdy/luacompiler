package com.jdy.lua.lcodes;

import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import static com.jdy.lua.lopcodes.Instructions.*;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lopcodes.OpMode;
import com.jdy.lua.lparser.*;

import static com.jdy.lua.LuaConstants.*;
import static com.jdy.lua.lopcodes.OpCode.*;

import java.util.List;

public class LCodes {

    /**
     * 对于跳转指令，跳转值为-1表示，等待回填具体的 值
     */
    public static final int NO_JUMP = -1;
    /**
     * 最大寄存器数量
     */
    public static final int MAX_REGS = 255;

    /**
     * 保存指令的行号信息
     *
     * lineinfos存放的 行号的偏移地址
     * 前一个指令的 绝对行号地址是  previousline
     * 当前指令的绝对行号地址是  line
     * 存储的是 line - previousline
     *
     */
    public static void saveLineInfo(FuncState fs,int line){
        int lineDif = line - fs.getPreviousline();
        fs.getProto().addLineInfo(lineDif);
        fs.setPreviousline(line);
    }
    public static int luaK_code(FuncState fs, Instruction i){
        fs.addInstruction(i);
        saveLineInfo(fs,fs.getLexState().getLastline());
        return fs.getPc() - 1;
    }
    public static int luaK_codeABC(FuncState fs, OpCode o,int a,int b,int c){
        return luaK_code(fs, create_ABCK(o.getCode(),a,b,c,0));
    }
    public static int luaK_codeABCk(FuncState fs, OpCode o,int a,int b,int c, int k){
        return luaK_code(fs, create_ABCK(o.getCode(),a,b,c,k));
    }

    public static Instruction getInstruction(FuncState f, ExpDesc expDesc){
        Proto proto = f.getProto();
        return proto.getInstruction(expDesc.getInfo());
    }

    public static boolean hasJumps(ExpDesc desc){
        return desc.getT() != desc.getF();
    }

    /**
     * 将 表达式 转成 numeric，如果可以的话
     */
    public static boolean tonumeral (ExpDesc e, TValue v) {
        if (hasJumps(e))
            return false;  /* not a numeral */
        switch (e.getK()) {
            case VKINT:
                if (v != null)v.setI(e.getIval());
                return true;
            case VKFLT:
                if (v != null) v.setN(e.getNval());
                return true;
            default: return false;
        }
    }

    /**
     * 根据 常量表达式 获取 常量
     */
    public static TValue const2Val(FuncState fs,ExpDesc desc){
        DynData dynData = fs.getLexState().getDyd();
        List<Vardesc> vardescList = dynData.getArr();
        if(desc.getK() == ExpKind.VCONST){
            return vardescList.get(desc.getInfo()).getK();
        }
        return null;
    }

    /**
     *如果表达式是常量，填充到v，返回true
     */
    public static boolean luaK_Exp2Const(FuncState fs, ExpDesc desc, TValue v){
        if(hasJumps(desc)){
            return false;
        }
        switch (desc.getK()){
            case VFALSE:v.setTt(LUA_TFALSE);break;
            case VTRUE:v.setTt(LUA_TTRUE);break;
            case VNIL:v.setTt(LUA_TNIL);break;
            case VKSTR:v.setTt(LUA_TSTRING);v.setP(desc.getStrval());break;
            case VCONST:v.initByTValue(const2Val(fs,desc));break;
            default:return tonumeral(desc,v);
        }
        return true;
    }

    /**
     * 无效的指令
     */
    public static Instruction INVALID_INSTRUCTIN = new Instruction(~0);

    /**
     * 返回上个指令
     * 如果有 jump target 在当前指令 和 前一个指令之间，返回一个无效指令
     */
    public static Instruction previousInstruction(FuncState fs){
        int pc = fs.getPc();
        Proto proto = fs.getProto();
        if(pc > fs.getLasttarget()){
            return proto.getInstruction(pc);
        }
        return INVALID_INSTRUCTIN;
    }

    /**
     * 创建一个 OP_LOADNIL 指令
     */
    public static void luaK_Nil(FuncState fs,int from, int n){
        luaK_codeABC(fs,OP_LOADNIL,from,n-1,0);
    }

    /**
     * 获取 jump指令，跳转到的绝对地址
     *
     */
    public static int getJumpDestination(FuncState f,int pc){
        Instruction jmp = f.getProto().getInstruction(pc);
        int offset = getArgsJ(jmp);
        if(offset == NO_JUMP){
            return NO_JUMP;
        }
        return pc + 1 + offset;
    }
    /**
     * jmp 指令，跳转到指定位置; 在知道跳转地址时，跳转位置是NO_JUMP，现在要进行调整
     */
    public static void fixJump(FuncState fs,int pc,int dest){
        Instruction jmp = fs.getProto().getInstruction(pc);
        //获取相对偏移量
        int offset = dest - (pc + 1);
        setArgsJ(jmp,offset);
    }
    /**
     * 连接 跳转列表
     * type == true 连接 真出口跳转表
     * type == false 连接 假出口 跳转表
     *
     * expDesc 的 t，f分表是真假跳转表的头部
     */
    public static void luaK_Concat(FuncState fs,ExpDesc expDesc,int l2, boolean type){
        if(l2 == NO_JUMP){
            return;
        } else if(type && expDesc.getT() == NO_JUMP){
            expDesc.setT(l2);
        } else if(!type && expDesc.getF() == NO_JUMP){
            expDesc.setF(l2);
        }else{
            int list = type ? expDesc.getT() : expDesc.getF();
            int next;
            //找到最后一个 jump语句
            while((next = getJumpDestination(fs,list)) != NO_JUMP){
                list = next;
            }
            //将最后一个jump语句的跳转到l2
            fixJump(fs,list,l2);
        }
    }

    /**
     * 创建一个 jmp指令
     */
    public static int luaK_Jump(FuncState fs){
        return codesJ(fs,OP_JMP,NO_JUMP,0);
    }
    /**
     * 创建一个 return 指令
     * nret 代表返回值数量
     */
    public static void luaK_Ret(FuncState fs,int first,int nret){
        OpCode op;
        switch (nret){
            case 0 :op = OP_RETURN0;break;
            case 1 : op = OP_RETURN1;break;
            default:op = OP_RETURN;
        }
        luaK_codeABC(fs,op,first,nret + 1,0);
    }
    /**
     * 创建一个 条件跳转指令 （内部共创建了两条指令，一条条件跳转，一条 无条件跳转jmp）
     */
    public static int condJump(FuncState fs,OpCode op,int a,int b,int c,int k){
        luaK_codeABCk(fs,op,a,b,c,k);
        return luaK_Jump(fs);
    }

    /**
     * 返回当前pc，将其标记为一个jump target， 也就是跳转的label，这里并没有真正创建一个label，而是使用了标记的方式
     */
    public static int luaK_GetLabel(FuncState fs){
        fs.setLasttarget(fs.getPc());
        return fs.getPc();
    }
    /**
     * 如果是条件跳转，jmp指令前面有一个 条件跳转指令，这里是获取跳转的条件指令
     * 如果无条件跳转，返回自身
     */
    public static Instruction getJumpCongtrol(FuncState fs,int pc){
        Proto proto = fs.getProto();
        Instruction ins = proto.getInstruction(pc);
        if(pc >=1 && OpMode.isTestOp(getOpCode(proto.getInstruction(pc - 1)))){
            return proto.getInstruction(pc - 1);
        }
        return ins;
    }

    /**
     * 创建一个 sj指令
     */
    public static int codesJ(FuncState fs, OpCode o,int sj,int k){
        //!! createSj时用的sj是 加上了 OFFSET_sj的
        int j = sj + OFFSET_sJ;
        return luaK_code(fs,create_sJ(o.getCode(),j,k));
    }




}
