package com.jdy.lua.lcodes2;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instructions;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lparser2.ArgAndKind;
import com.jdy.lua.lparser2.FunctionInfo;
import com.jdy.lua.lparser2.expr.*;
import com.jdy.lua.lparser2.statement.Statement;
import com.sun.org.apache.xpath.internal.Arg;

public class InstructionGenerator {

   private FunctionInfo fi;

   public InstructionGenerator(FunctionInfo fi){
       this.fi = fi;
   }
    /**
     *用于 存储 fi中的寄存器数量
     */
   private int oldRegs;

    /**
     * 存储寄存器
     */
   public void storeRegs(){
       oldRegs = fi.getUsedRegs();
   }

    /**
     * 还原寄存器
     */
   public void loadRegs(){
       fi.setUsedRegs(oldRegs);
   }


   public void generate(Expr expr,int a,int n){

   }
   public void generate(Statement statement,int a,int n){

   }
   public void generate(TableIndex index,int a,int n){

   }
   public void generate(SubExpr subExpr, int a,int n){
       int b = -1;
       //有单个操作符，需要优先进行处理
        if(subExpr.getUnOpr() != null && subExpr.getUnOpr() != UnOpr.OPR_NOUNOPR){
            storeRegs();
            //将表达式存进寄存器b里面去
            b = exp2ArgAndKind(fi,subExpr.getSubExpr1(),ArgAndKind.ARG_REG).getArg();
            switch (subExpr.getUnOpr()) {
                case OPR_NOT:  Lcodes.emitCodeABC(fi,OpCode.OP_NOT,a,b,0); break;
                case OPR_BNOT: Lcodes.emitCodeABC(fi,OpCode.OP_BNOT,a,b,0); break;
                case OPR_LEN:  Lcodes.emitCodeABC(fi,OpCode.OP_LEN,a,b,0); break;
                case OPR_MINUS:  Lcodes.emitCodeABC(fi,OpCode.OP_UNM,a,b,0); break;
                default:break;
            }
            loadRegs();
        }

        //表示只有一个操作数
        if(subExpr.getBinOpr() == null || subExpr.getBinOpr() == BinOpr.OPR_NOBINOPR){
            return;
        }
        //接着处理第二个操作数, and 和 or单独处理
       if(subExpr.getBinOpr() == BinOpr.OPR_AND || subExpr.getBinOpr() == BinOpr.OPR_OR){
           //表示左边的第一个表达式还未处理
             if(b == -1){
               storeRegs();
               b = exp2ArgAndKind(fi,subExpr.getSubExpr1(),ArgAndKind.ARG_REG).getArg();
               loadRegs();
             }
             //and 和 or 的跳转方向相反
             if(subExpr.getBinOpr() == BinOpr.OPR_AND){
                 Lcodes.emitCodeABC(fi,OpCode.OP_TESTSET,a,b,0);
             } else{
                 Lcodes.emitCodeABC(fi,OpCode.OP_TESTSET,a,b,1);
             }
             int jmpPc = Lcodes.emitCodeJump(fi,0,0);

             b = exp2ArgAndKind(fi,subExpr.getSubExpr2(),ArgAndKind.ARG_REG).getArg();
             loadRegs();
             Lcodes.emitCodeABC(fi,OpCode.OP_MOVE,a,b,0);
             //获取当前指令的位置
             int curPc = fi.getPc();
             Instructions.setArgsJ(fi.getInstruction(jmpPc),curPc);
       //统一处理其他操作符
       } else{
           if(b == -1){
               storeRegs();
               b = exp2ArgAndKind(fi,subExpr.getSubExpr1(),ArgAndKind.ARG_REG).getArg();
           }
           int c = exp2ArgAndKind(fi,subExpr.getSubExpr2(), ArgAndKind.ARG_REG).getArg();
           Lcodes.emitBinaryOp(fi,subExpr.getBinOpr(),a,b,c);
           loadRegs();
       }
   }
   public void generate(SimpleExpr expr ,int a,int n){
       expr.getExpr().generate(this,a,n);
   }
   public void generate(VarargExpr expr,int a,int n){
       Lcodes.emitCodeABC(fi,OpCode.OP_VARARG,a,n+1,0);
   }

    public void generate(NilExpr expr,int a,int n){
        Lcodes.emitCodeABC(fi,OpCode.OP_LOADNIL,a,n-1,0);
    }
    public void generate(TrueExpr expr,int a,int n){
        Lcodes.emitCodeABC(fi,OpCode.OP_LOADTRUE,a,0,0);
    }
    public void generate(FalseExpr expr,int a,int n){
        Lcodes.emitCodeABC(fi,OpCode.OP_LOADFALSE,a,0,0);
    }
    public void generate(FloatExpr expr,int a,int n){
        int k = fi.indexOfConstant(TValue.doubleValue(expr.getF()));
        Lcodes.emitCodeK(fi,a,k);
    }
    public void generate(IntExpr expr,int a,int n){
        int k = fi.indexOfConstant(TValue.intValue(expr.getI()));
        Lcodes.emitCodeK(fi,a,k);
    }
    public void generate(StringExpr expr,int a,int n){
        int k = fi.indexOfConstant(TValue.strValue(expr.getStr()));
        Lcodes.emitCodeK(fi,a,k);
    }
    /**
     * 将 表达式进行处理，结果存储在 返回的 ArgAndKind对象里面 kind表示，存储的类型，
     */
    public ArgAndKind exp2ArgAndKind(FunctionInfo fi,Expr expr,int kind){
        if(expr instanceof NameExpr){
            //从函数的 localVar中查找变量
            if((kind & ArgAndKind.ARG_REG) != 0){

            }
            //从 UpVal中查找变量
            if((kind & ArgAndKind.ARG_UPVAL) != 0){

            }
        }
        int a = fi.allocReg();
        expr.generate(this,a,1);
        return new ArgAndKind(a,ArgAndKind.ARG_REG);
    }






}
