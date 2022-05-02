package com.jdy.lua.lopcodes;

@SuppressWarnings("all")
public class Instructions {
    /**
     * 定义各种参数的position 和  size
     */
    public static int SIZE_C = 8;

    public static int SIZE_B = 8;

    public static int SIZE_BX = SIZE_C + SIZE_B + 1;

    public static int SIZE_A = 8;

    public static int SIZE_Ax = SIZE_BX + SIZE_A;

    public static int SIZE_sJ = SIZE_BX + SIZE_A;
    /**
     * 字节码长度
     */
    public static int SIZE_OP = 7;
    /**
     * 字节码位置
     */
    public static int POS_OP = 0;

    public static int POS_A = POS_OP + SIZE_OP;

    public static int POS_k = POS_A + SIZE_A;

    public static int POS_B =  POS_k + 1;

    public static int POS_C = POS_B + SIZE_B;

    public static int POS_Bx = POS_k;

    public static int POS_Ax = POS_A;

    public static int POS_sJ = POS_A;

    public static int MAX_ARG_Bx = (1 << SIZE_BX) - 1;

    public static int OFFSET_sBx = MAX_ARG_Bx >> 1;

    public static int MAX_ARG_Ax = (1 << SIZE_Ax) - 1;

    public static int OFFSET_sAx = MAX_ARG_Ax >> 1;

    public static int MAX_ARG_A = (1 << SIZE_A) - 1;

    public static int MAX_ARG_B = (1 << SIZE_B) - 1;

    public static int MAX_ARG_C = (1 << SIZE_C) - 1;

    public static int OFFSET_sC = MAX_ARG_C >> 1;

    public static int MAX_ARG_sJ = (1 << SIZE_sJ) - 1;

    public static int OFFSET_sJ = MAX_ARG_sJ >> 1;

    /**
     * 最大寄存器数量
     */
    public static int MAX_REGISTER_NUM = MAX_ARG_A;

    public static int MAX_INDEX_RK = MAX_ARG_B;


    /**
     * 定义一些静态方法
     */

    public static int int2Sc(int i){
        return i + OFFSET_sC;
    }

    public static int sc2int(int i){
        return i - OFFSET_sC;
    }

    /**
     *创建一个掩码， n 个 1 bits in position p
     */
    public static int mask1(int n,int p){
        return ((~((~(0)<<(n)))<<(p)));
    }

    public static int mask0(int n,int p){
        return ~mask1(n,p);
    }

    /**
     * 获取指令集的 OpCode
     */
    public static OpCode getOpCode(Instruction instruction){
        int op = instruction.ins & 0x7f;
        return getOpCode(op);
    }
    public static OpCode getOpCode(int i){
        return OpCode.getOpCode(i);
    }

    public static void setOpCode(Instruction instruction,OpCode opCode){
        int code = opCode.getCode();
        instruction.ins = (instruction.ins & mask0(SIZE_OP,POS_OP)) | ((code << POS_OP) & mask1(SIZE_OP,POS_OP));
    }
    public static OpMode getOpMode(int opcode){
        return OpMode.getOpMode(OpMode.lua_opmodes[opcode] & 7);
    }
    public boolean checkOpMode(OpCode opCode,OpMode opMode){
        return getOpMode(opCode.getCode()) == opMode;
    }

    /**
     * 通过位运算，处理参数的值
     */
    public static int getArg(Instruction i,int pos,int size){
        return (i.ins >> pos) & mask1(size,0);
    }
    public static void setArg(Instruction i, int v , int pos, int size){
        i.ins = (i.ins & mask0(size,pos)) | ((v << pos) & mask1(size,pos));
    }

    public static int getArgA(Instruction i){
        return getArg(i,POS_A,SIZE_A);
    }

    public static void  setArgA(Instruction i, int v){
        setArg(i,v, POS_A,SIZE_A);
    }

    public static int getArgB(Instruction i){
        return getArg(i,POS_B,SIZE_B);
    }

    public static void  setArgB(Instruction i, int v){
        setArg(i,v, POS_B,SIZE_B);
    }

    public static int getArgC(Instruction i){
        return getArg(i,POS_C,SIZE_C);
    }

    public static void  setArgC(Instruction i, int v){
        setArg(i,v, POS_C,SIZE_C);
    }

    public static int getArg_sC(Instruction instruction){
        return sc2int(getArgC(instruction));
    }

    public static int getArgk(Instruction i){
        return getArg(i,POS_k,1);
    }

    public static void  setArgk(Instruction i, int v){
        setArg(i,v, POS_k,1);
    }

    public static int getArgBx(Instruction i){
        return getArg(i,POS_Bx,SIZE_BX);
    }

    public static void  setArgBx(Instruction i, int v){
        setArg(i,v, POS_Bx,SIZE_BX);
    }

    public static int getArgAx(Instruction i){
        return getArg(i,POS_Ax,SIZE_Ax);
    }

    public static void  setArgAx(Instruction i, int v){
        setArg(i,v, POS_Ax,SIZE_Ax);
    }

    public static int getArgsBx(Instruction i){
        return getArg(i,POS_Bx,SIZE_BX) - OFFSET_sBx;
    }

    public static void  setArgsBx(Instruction i, int v){
        setArg(i,v + OFFSET_sBx, POS_Bx,SIZE_BX);
    }

    public static int getArgsJ(Instruction i){
        return getArg(i,POS_sJ,SIZE_sJ) - OFFSET_sJ;
    }

    public static void  setArgsJ(Instruction i, int v){
        setArg(i,v + OFFSET_sJ, POS_sJ,SIZE_sJ);
    }

    /**
     * 创建Instruction的方法
     */
    public static Instruction create_ABCK(int o,int a,int b,int c,int k){
        int instruction = (o << POS_OP) | (a << POS_A) | ( b << POS_B) | (c << POS_C) | (k << POS_k);
        return new Instruction(instruction);
    }

    public static Instruction create_ABx(int o,int a, int bx){
        int instruction = (o << POS_OP) | (a << POS_A) | (bx << POS_Bx);
        return new Instruction(instruction);
    }

    public static Instruction create_Ax(int o,int ax){
        int instruction = (o << POS_OP) | (ax << POS_Ax);
        return new Instruction(instruction);
    }

    public static Instruction create_sJ(int o,int sj,int k){
        int instruction = (o << POS_OP) | (sj << POS_sJ) | (k << POS_k);
        return new Instruction(instruction);
    }
}
