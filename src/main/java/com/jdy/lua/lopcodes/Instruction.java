package com.jdy.lua.lopcodes;

import static com.jdy.lua.lopcodes.Instructions.*;

/**
 * 定义指令
 */

public class Instruction {
    int ins;
    public int getIns() {
        return ins;
    }
    public void setIns(int i){
        this.ins = i;
    }
    public Instruction(){

    }
    public Instruction(int i){
        this.ins = i;
    }





    public static void main(String[] args) {

        Instruction instruction = create_sJ(OpCode.OP_ADD.getCode(),-1,0);
        System.out.println(getArgsJ(instruction));

    }

    @Override
    public String toString() {
        OpCode op = getOpCode(this);
        OpMode mode =OpMode.getOpMode(op);
        StringBuilder sb = new StringBuilder();
        sb.append(op).append(":");
        if(mode == OpMode.iABC){
            int a =getArgA(this);
            int b = getArgB(this);
            int c = getArgC(this);
            int k = getArgk(this);
            sb.append("a= ").append(a).append(" b=").append(b).append(" c=").append(c).append("k=").append(k);
        } else if(mode == OpMode.iABx){
            int a = getArgA(this);
            int bx = getArgBx(this);
            int k = getArgk(this);
            sb.append("a= ").append(a).append(" bx= ").append(bx)
                    .append(" k=").append(k);
        } else if(mode == OpMode.iAsBx){
            int a =getArgA(this);
            int b = getArgsBx(this);
            int k = getArgk(this);
            sb.append("a= ").append(a).append(" sbx= ").append(b)
                    .append(" k=").append(k);
        } else if(mode == OpMode.iAx){
            int a = getArgAx(this);
            sb.append("a= ").append(a);
        } else {
            int sj =getArgsJ(this);
            sb.append("sj=").append(" ").append(sj);
        }
        return sb.toString();
    }
}
