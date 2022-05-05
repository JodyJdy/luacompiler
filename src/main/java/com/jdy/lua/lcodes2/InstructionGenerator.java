package com.jdy.lua.lcodes2;

import com.jdy.lua.lcodes.LCodes;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.LocalVar;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lparser.DynData;
import static com.jdy.lua.lparser.ExpKind.*;

import com.jdy.lua.lparser.ExpKind;
import com.jdy.lua.lparser.FuncState;
import com.jdy.lua.lparser.Vardesc;
import com.jdy.lua.lparser2.LocalVarAttribute;
import com.jdy.lua.lparser2.expr.*;
import com.jdy.lua.lparser2.statement.LocalStatement;
import com.jdy.lua.lparser2.statement.Statement;
import com.jdy.lua.lstate.LuaState;

import java.util.ArrayList;
import java.util.List;

import static com.jdy.lua.lopcodes.OpCode.OP_MOVE;
import static com.jdy.lua.lparser.ParserConstants.*;

public class InstructionGenerator {

    FuncState fs;
    DynData dynData;

    public GenerateInfo generate(Statement statement){
        return null;
    }

    private   Vardesc  getLocalVarDesc(FuncState fs,int vidx){
        LexState ls = fs.getLexState();
        DynData dynData = ls.getDyd();
        return dynData.getVarDesc(fs.getFirstlocal() + vidx);
    }

    /**
     * 返回变量的数目
     */
    private int varNum(FuncState fs){
        return 1 + getLocalVarDesc(fs,fs.getNactvar()).getRidx();
    }
    /**
     * 申请n个寄存器，返回第一个可用的寄存器下标
     */
    private int reserveRegs(int n){
        int free =fs.getFreereg();
        fs.setFreereg(fs.getFreereg() + n);
        return free;
    }
    private int freeReg(int n){
        for(int i=0;i<n;i++){
            fs.setFreereg(fs.getFreereg()-1);
        }
        return fs.getFreereg();
    }
    private int registerLocalVar(NameExpr expr){
        Vardesc vardesc = new Vardesc();
        vardesc.setName(expr.getName());
        vardesc.setKind(VDKREG);
        //设置所属的寄存器
        vardesc.setRidx(dynData.getActiveLocVarSize());
        dynData.addVarDesc(vardesc);

        Proto proto = fs.getProto();
        LocalVar localVar = new LocalVar();
        localVar.setName(expr.getName());
        localVar.setStartpc(fs.getPc());
        //设置新变量在proto中的下标
        vardesc.setPidx(proto.getLocaVarSize());
        proto.addLocalVar(localVar);
        fs.setNactvar(fs.getNactvar() + 1);
        //申请一个寄存器用来存放变量新创建的变量
        reserveRegs(1);
        //返回变量的寄存器索引
        return vardesc.getRidx();

    }


    public GenerateInfo generate(LocalStatement localStatement){

        List<NameExpr> nameExprList =localStatement.getNameExprList();
        List<Expr> exprList = localStatement.getExprList().getExprList();
        int exprSize = exprList.size();
        //变量数目 和 表达式数目对不上时，赋值nil
        while(exprSize++ < nameExprList.size()){
            exprList.add(new NilExpr());
        }

        List<Integer> varIndexs = new ArrayList<>();
        //注册变量
        for (NameExpr nameExpr : nameExprList) {
            varIndexs.add(registerLocalVar(nameExpr));
        }
        int freeReg = fs.getFreereg();
        List<GenerateInfo> infos = new ArrayList<>();
        //将表达式生成的结果进行存储
        for(int i=0;i<nameExprList.size();i++){
           infos.add(exprList.get(i).generate(this));
        }
        //生成赋值语句
        for(int i = 0;i<varIndexs.size();i++){
            LCodes.luaK_codeABC(fs, OP_MOVE,varIndexs.get(i),infos.get(i).getRegIndex(), 0);
        }
        //释放掉占用的寄存器
        freeReg(fs.getFreereg() - freeReg);
        return null;
    }
    public GenerateInfo generate(Expr expr){
        return null;
    }
    public GenerateInfo generate(Expr expr,GenerateInfo generateInfo){
        return null;
    }

    public GenerateInfo generate(ExprList exprList){
        return null;
    }

    /**
     * 常量的处理
     */
    public GenerateInfo generate(TrueExpr expr){
       return GenerateInfo.info(VTRUE);
    }
    public GenerateInfo generate(FalseExpr expr){
       return GenerateInfo.info(VFALSE);
    }
    public GenerateInfo generate(IntExpr expr){
        return GenerateInfo.intInfo(expr.getI());
    }
    public GenerateInfo generate(FloatExpr expr){
        return GenerateInfo.floatInfo(expr.getF());
    }
    public GenerateInfo generate(StringExpr expr){
       return GenerateInfo.strInfo(expr.getStr());
    }
    public GenerateInfo generate(NilExpr expr){
       return GenerateInfo.info(VNIL);
    }

    public GenerateInfo generate(SimpleExpr expr){

    }


}
