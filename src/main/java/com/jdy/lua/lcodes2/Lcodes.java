package com.jdy.lua.lcodes2;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lparser.*;
import com.jdy.lua.lparser2.FunctionInfo;

import java.util.List;

import static com.jdy.lua.LuaConstants.*;
import static com.jdy.lua.LuaConstants.LUA_TSTRING;
import static com.jdy.lua.lcodes.BinOpr.OPR_ADD;
import static com.jdy.lua.lopcodes.Instructions.*;
import static com.jdy.lua.lopcodes.OpCode.*;

import static com.jdy.lua.lcodes.LCodes.*;
import static com.jdy.lua.lparser.ExpKind.*;

public class Lcodes {

    public static int emitCode(FunctionInfo fi, Instruction i){
       return fi.addInstruction(i);
    }
    public static int emitCodeABC(FunctionInfo fi, OpCode o, int a, int b, int c){
        return emitCode(fi,create_ABCK(o.getCode(),a,b,c,0));
    }
    public static int emitCodeABCK(FunctionInfo fi, OpCode o,int a,int b,int c, int k){
        return emitCode(fi,create_ABCK(o.getCode(),a,b,c,k));
    }

    public static int emitCodeABx(FunctionInfo fi, OpCode o,int a,int bx){
        //无符号的Bx
        return emitCode(fi,create_ABx(o.getCode(),a,bx));
    }

    public static int emitCodeSbx(FunctionInfo fi, OpCode o,int a,int bc){
        //有符号的Bx
        int b = bc + OFFSET_sBx;
        return emitCode(fi,create_ABx(o.getCode(),a,b));
    }

    /**
     * iAx
     */
    public static int codeExtraArg(FunctionInfo fi,int a){
        return emitCode(fi,create_Ax(OP_EXTRAARG.getCode(),a));
    }

    /**
     * 生成一个 load constant的指令
     * 如果 indexForTable constants 小于18 bit，使用OP_LOADK
     * 否则就是OP_LOADKX,并且，额外生成一条OP_EXTRAARG指令
     */
    public static int emitCodeK (FunctionInfo fi, int reg, int k) {
        if (k <= MAX_ARG_Bx)
            return emitCodeABx(fi, OP_LOADK, reg, k);
        else {
            int p = emitCodeABx(fi, OP_LOADKX, reg, 0);
            codeExtraArg(fi, k);
            return p;
        }
    }
    public static int codesJ(FunctionInfo fi, OpCode o,int sj,int k){
        //!! createSj时用的sj是 加上了 OFFSET_sj的
        int j = sj + OFFSET_sJ;
        return emitCode(fi,create_sJ(o.getCode(),j,k));
    }
    public static int emitCodeJump(FunctionInfo fi,int sJ,int k){
        return codesJ(fi,OpCode.OP_JMP,sJ,k);
    }

    public static void emitBinaryOp(FunctionInfo fi, BinOpr op,int a,int b, int c){
        switch (op){
            case OPR_ADD:
            case OPR_SUB:
            case OPR_MUL:
            case OPR_DIV:
            case OPR_IDIV:
            case OPR_MOD:
            case OPR_POW:
            case OPR_BAND:
            case OPR_BOR:
            case OPR_BXOR:
            case OPR_SHL:
            case OPR_SHR:
                OpCode opCode = OpCode.getOpCode(op.getOp() - OPR_ADD.getOp() + OP_ADD.getCode());
                emitCodeABC(fi,opCode,a,b,c);
                break;
            case OPR_CONCAT:
                emitCodeABC(fi, OP_CONCAT,b,2,0);
            default:
                switch (op){
                    case OPR_EQ:emitCodeABC(fi,OP_EQ,b,c,1);break;
                    case OPR_NE:emitCodeABC(fi,OP_EQ,b,c,0);break;
                    case OPR_LT:emitCodeABC(fi,OP_LT,b,c,1);break;
                    case OPR_LE:emitCodeABC(fi,OP_LE,b,c,1);break;
                    case OPR_GE:emitCodeABC(fi,OP_LT,b,c,0);break;
                    case OPR_GT:emitCodeABC(fi,OP_LE,b,c,0);break;
                    default:break;
                }
                emitCodeJump(fi,1,0);
                emitCodeABC(fi,OpCode.OP_LFALSESKIP,a,0,0);
                emitCodeABC(fi, OP_LOADTRUE,a,0,0);
        }
    }
}
