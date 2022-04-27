package com.jdy.lua.lcodes;

import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TString;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import static com.jdy.lua.lopcodes.Instructions.*;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lopcodes.OpMode;
import com.jdy.lua.lparser.*;

import static com.jdy.lua.LuaConstants.*;
import static com.jdy.lua.lopcodes.OpCode.*;
import static com.jdy.lua.lparser.ExpKind.*;

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
     * 无寄存器使用
     */
    public static final int NO_REG = MAX_REGS;

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

    public static int luaK_codeABx(FuncState fs, OpCode o,int a,int bc){
        //无符号的Bx
        return luaK_code(fs,create_ABx(o.getCode(),a,bc));
    }

    public static int luaK_codeAsBx(FuncState fs, OpCode o,int a,int bc){
        //有符号的Bx
        int b = bc + OFFSET_sBx;
        return luaK_code(fs,create_ABx(o.getCode(),a,b));
    }

    /**
     * iAx
     */
    public static int codeExtraArg(FuncState fs,int a){
        return luaK_code(fs,create_Ax(OP_EXTRAARG.getCode(),a));
    }

    /**
     * 生成一个 load constant的指令
     * 如果 index k 小于18 bit，使用OP_LOADK
     * 否则就是OP_LOADKX,并且，额外生成一条OP_EXTRAARG指令
     */
    static int luaK_codeK (FuncState fs, int reg, int k) {
        if (k <= MAX_ARG_Bx)
            return luaK_codeABx(fs, OP_LOADK, reg, k);
        else {
            int p = luaK_codeABx(fs, OP_LOADKX, reg, 0);
            codeExtraArg(fs, k);
            return p;
        }
    }

    /**
     * 检查寄存器 stack的数目
     */
    public static void luaK_checkStack(FuncState fs,int n){
        int newStack = fs.getFreereg() + n;
        if(newStack > fs.getProto().getMaxstacksize()){
            if(newStack > MAX_REGS){
                System.err.println("寄存器达到最大值");
            }
            //重新设置 最大栈的尺寸
            fs.getProto().setMaxstacksize(newStack);
        }
    }

    /**
     * 申请n个寄存器使用
     */
    public static void luaK_reserveRegs(FuncState fs,int n){
        luaK_checkStack(fs,n);
        fs.setFreereg(fs.getFreereg() + n);
    }

    /**
     * 释放寄存器 reg。  如果它既不是 local变量也不是常量索引
     */
    public static void freeReg (FuncState fs, int reg) {
        // TODO: 2022/4/27  luaY_nvarstack函数还未实现，需要后面补充
//        if (reg >= luaY_nvarstack(fs)) {
          fs.decreFreeReg();
//        }
    }

    public static void freeRegs (FuncState fs, int r1, int r2) {
        if (r1 > r2) {
            freeReg(fs, r1);
            freeReg(fs, r2);
        }
        else {
            freeReg(fs, r2);
            freeReg(fs, r1);
        }
    }

    /**
     ** 释放 表达式 e 使用的寄存器
     */
    public static void freeExpReg (FuncState fs, ExpDesc e) {
        if (e.getK() == VNONRELOC){
            freeReg(fs, e.getInfo());
        }
    }


    /**
       释放 表达式 e1,e2使用的表达式
     */
    static void freeexps (FuncState fs, ExpDesc e1, ExpDesc e2) {
        int r1 = (e1.getK() == VNONRELOC) ? e1.getInfo() : -1;
        int r2 = (e2.getK() == VNONRELOC) ? e2.getInfo() : -1;
        freeRegs(fs, r1, r2);
    }

    /**
     * 加入一个 产量到 Proto 的常量数组里面
     * LexState的h表，存储了 常量->常量数组的下标
     */
    public static int addK(FuncState fs,TValue key,TValue value){
        TValue val = new TValue();
        Proto proto = fs.getProto();
        LexState lex = fs.getLexState();
        TValue idx = lex.getH().get(key);
        if(idx != null){
            return (int) idx.getI();
        }
        //创建一个新的常量到常量数组里面
        int k = fs.getNk();
        val.setI(k);
        //将值 和 下标，存储
        lex.getH().put(value,val);
        //存储到常量数组里面去
        proto.getK().add(value);
        fs.setNk(k + 1);
        return k;
    }

    public static int stringK(FuncState fs, TString t){
        TValue v = TValue.strValue(t);
        return addK(fs,v,v);
    }

    public static int luaK_intK(FuncState fs,long n){
        TValue o = TValue.intValue(n);
        return addK(fs,o,o);
    }

    public static int luaK_Number(FuncState fs,double n){
        TValue v = TValue.doubleValue(n);
        return addK(fs,v,v);
    }

    public static int boolF(FuncState fs){
        TValue v = TValue.falseValue();
        return addK(fs,v,v);
    }

    public static int boolT(FuncState fs){
        TValue v = TValue.trueValue();
        return addK(fs,v,v);
    }

    /**
     * 检查 i 能否存储在 sC 操作数上面
     */
    public static boolean fitsC(long i){
        return i + OFFSET_sC < MAX_ARG_C;
    }

    /**
     ** 检查i 能否存储在 sBx 操作数上面
     */
    public static boolean fitsBx (long i) {
        return (-OFFSET_sBx <= i && i <= MAX_ARG_Bx - OFFSET_sBx);
    }

    /**
     * 如果数值大小，能够存储在 Instruction里面， 就用 loadi
     */
    public  static void luaK_int (FuncState fs, int reg, long i) {
        if (fitsBx(i))
            luaK_codeAsBx(fs, OP_LOADI, reg, (int)i);
        else
            luaK_codeK(fs, reg, luaK_intK(fs, i));
    }

    /**
     * 如果数值大小，能够存储在 Instruction里面， 就用 loadf
     */

    public static void luaK_Float(FuncState fs,int reg,double f){
        long i = (long)f;
        //如果浮点数是 10.0 这种，没有尾数的，直接存储整数
        if(Math.floor(f) == i && fitsBx(i)){
            luaK_codeAsBx(fs,OP_LOADF,reg,(int)i);
        } else{
            //当成常量存
            luaK_codeK(fs,reg,luaK_Number(fs,f));
        }
    }

    /**
     * 常量转成 Expdesc
     */
    public static void const2Exp(TValue v,ExpDesc expDesc){
        switch (v.getValueType()) {
            case LUA_TNUMINT:
                expDesc.setK(VKINT);
                expDesc.setIval(v.getI());
                break;
            case LUA_TNUMFLONT:
                expDesc.setK(VKFLT);
                expDesc.setNval(v.getF());
                break;
            case LUA_TFALSE:
                expDesc.setK(VFALSE);
                break;
            case LUA_TTRUE:
                expDesc.setK(VTRUE);
                break;
            case LUA_TNIL:
                expDesc.setK(VKINT);
                break;
            case LUA_TSTRING:
                expDesc.setK(VKSTR);
                expDesc.setStrval((String)v.getObj());
                break;
            default: break;
        }
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
                if (v != null) v.setF(e.getNval());
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
            case VFALSE:v.setValueType(LUA_TFALSE);break;
            case VTRUE:v.setValueType(LUA_TTRUE);break;
            case VNIL:v.setValueType(LUA_TNIL);break;
            case VKSTR:v.setValueType(LUA_TSTRING);v.setObj(desc.getStrval());break;
            case VCONST:v.initByTValue(const2Val(fs,desc));break;
            default:return tonumeral(desc,v);
        }
        return true;
    }

    /**
     * 设置函数 调用 返回值的数量 或者 指令OP_VARARG中 可变参数参数值的数量
     */
    public static  void luaK_setReturns(FuncState fs,ExpDesc e,int nResults){
        Instruction pc = getInstruction(fs,e);
        // 函数调用
        if(e.getK() == VCALL){
            setArgC(pc, nResults + 1);
        } else{
            // VVararg
            setArgC(pc,nResults + 1);
            setArgA(pc,fs.getFreereg());
        }
    }

    /**
     * 字符串表达式，转常量；
     * info存储，字符串常量的下标
     */
    public static void str2K(FuncState fs, ExpDesc e){
        e.setInfo(stringK(fs,new TString(e.getStrval())));
        e.setK(VK);
    }
    /**
     * 修正 返回一个结果的表达式；
     * 如果表达式不是一个多返回值的结果，就无需处理
     */
    public void luaK_setOneRet(FuncState fs,ExpDesc e){
        if(e.getK() == VCALL){
            //函数调用会变成 VNONRELOC,表示函数调用的结果需要存放在固定的寄存器里面
            e.setK(VNONRELOC);
            e.setInfo(getArgA(getInstruction(fs,e)));
        } else if(e.getK()== VVARARG){
            setArgC(getInstruction(fs,e),2);
            //表达式的结果可以存放在任意的寄存器里
            e.setK(VRELOC);
        }
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
     *设置 TESTSET值所存放的寄存器
     */
    public static boolean patchTestReg(FuncState fs,int node,int reg){
        //传入的node是一个jmp指令，根据jmp指令获取到 jumpcontrol指令
        Instruction i = getJumpCongtrol(fs,node);
        if(getOpCode(i) != OP_TESTSET){
            return false;
        }
        // R[A]=R[B]，所以不同是同一个寄存器
        if(reg != NO_REG && reg != getArgB(i)){
            setArgA(i,reg);
        } else{
            //无寄存器存放value 或者 A==B,值已经在寄存器里面了，将指令调整为TEST
            i.setIns(create_ABCK(OP_TEST.getCode(),getArgB(i),0,0,getArgk(i)).getIns());
        }
        return true;
    }

    /**
     * 遍历一系列 test 指令，确保没有指令会 产生值
     */
    public static void removeValues(FuncState fs,int list){
        for(; list != NO_JUMP;list = getJumpDestination(fs,list)){
            patchTestReg(fs,list,NO_REG);
        }
    }
    /**
     * 遍历一系列 test指令， 调整 跳转地址和寄存器；
     *  会产生value的 test 跳转到vtarget，值存放在寄存器 reg
     *  其他的跳转到dtarget
     */
    public static void patchListAux(FuncState fs,int list,int vtarget,int reg,int dtarget){
        while (list != NO_JUMP){
            int next =getJumpDestination(fs,list);
            //patchTestReg处理了 jumpcontro指令，需要再去处理jump指令，也就是 list
            if(patchTestReg(fs,list,reg)){
                fixJump(fs,list,vtarget);
            } else{
                //跳转到默认的target
                fixJump(fs,list,dtarget);
            }
            list = next;
        }
    }
    /**
     * 将 list 中的所有 jump ，都跳转到 target
     *
     * 函数会让所有产生值的TESTSET替换成TEST，
     */
    public static void luaK_patchList(FuncState fs,int list,int target){
        patchListAux(fs,list,target,NO_REG,target);
    }
    /**
     * 跳转到当前位置
     */
    public static void luaK_patchToHere(FuncState fs,int list){
        //获取一个跳转用的标签
        int here =luaK_GetLabel(fs);
        luaK_patchList(fs,list,here);
    }

    /**
     * 移除上一个指令的行号信息
     */
    public static void removeLastLineInfo(FuncState fs){
        Proto proto = fs.getProto();
        int pc = fs.getPc() - 1;
        fs.setPreviousline(fs.getPreviousline() - proto.getLineInfo(pc));
    }
    /**
     * 移除上一个指令
     */
    public static void removeLastInstruction(FuncState fs){
        removeLastLineInfo(fs);
        fs.removeLastInstruciton();
    }

    /**
     * 创建一个 sj指令
     */
    public static int codesJ(FuncState fs, OpCode o,int sj,int k){
        //!! createSj时用的sj是 加上了 OFFSET_sj的
        int j = sj + OFFSET_sJ;
        return luaK_code(fs,create_sJ(o.getCode(),j,k));
    }


    public static void main(String[] args) {

        byte b;

    }




}
