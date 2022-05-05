package com.jdy.lua.lcodes2;

import com.jdy.lua.lcodes.LCodes;
import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.LocalVar;
import com.jdy.lua.lobjects.Proto;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lparser.DynData;
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

    /**
     * 将表达式的结果存储在指定寄存器里面
     */
    public void expr2reg(Expr expr,int reg){
        expr.generate(this);
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

    public GenerateInfo generate(ExprList exprList){
        return null;
    }

    /**
     * 常量的处理
     */
    public GenerateInfo generate(TrueExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(trueConstant(fs));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }
    public GenerateInfo generate(FalseExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(falseConstant(fs));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }
    public GenerateInfo generate(IntExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(intConstant(fs,expr.getI()));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }
    public GenerateInfo generate(FloatExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(floatConstant(fs,expr.getF()));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }
    public GenerateInfo generate(StringExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(stringConstant(fs,expr.getStr()));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }
    public GenerateInfo generate(NilExpr expr){
        GenerateInfo info = new GenerateInfo(LocalVarAttribute.RDKCONST);
        info.setKIndex(nilConstant(fs));
        LCodes.luaK_codeK(fs,reserveRegs(1),info.getKIndex());
        return info;
    }

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

    private static int intConstant(FuncState fs,long n){
        TValue o = TValue.intValue(n);
        return addK(fs,o,o);
    }

    private static int floatConstant(FuncState fs,double n){
        TValue v = TValue.doubleValue(n);
        return addK(fs,v,v);
    }

    private static int falseConstant(FuncState fs){
        TValue v = TValue.falseValue();
        return addK(fs,v,v);
    }

    private static int trueConstant(FuncState fs){
        TValue v = TValue.trueValue();
        return addK(fs,v,v);
    }

    private static int nilConstant(FuncState fs){
        TValue v = TValue.nilValue();
        return addK(fs,v,v);
    }
}
