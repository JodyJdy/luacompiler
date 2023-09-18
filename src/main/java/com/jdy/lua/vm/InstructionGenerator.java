package com.jdy.lua.vm;

import com.jdy.lua.data.NumberValue;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;

import static com.jdy.lua.vm.ByteCode.*;

/**
 *
 * 用于生成字节码指令
 * @author jdy
 * @title: InstructionGenerator
 * @description:
 * @data 2023/9/18 14:15
 */
public class InstructionGenerator {

    private final FuncInfo currentFuncInfo;

    public InstructionGenerator(FuncInfo currentFuncInfo) {
        this.currentFuncInfo = currentFuncInfo;
    }

    public void generateStatement(Statement statement){
        if (statement instanceof Statement.IfStatement ifStatement) {
            DynamicLabel trueLabel = new DynamicLabel();
            DynamicLabel falseLabel = new DynamicLabel();
            DynamicLabel endLabel = new DynamicLabel();
            int reg1 = generateExpr(ifStatement.getIfCond());
            currentFuncInfo.addCode(new TEST(reg1));
            //跳到假出口
            currentFuncInfo.addCode(new JMP(falseLabel));
            //执行。。。。
            //跳到结尾位置
            currentFuncInfo.addCode(new JMP(endLabel));
        }
        if (statement instanceof Statement.FunctionStatement func) {
            FuncInfo funcInfo = new FuncInfo(currentFuncInfo);
            InstructionGenerator instructionGenerator = new InstructionGenerator(funcInfo);
            instructionGenerator.generateStatement(func.getFuncBody().getBlockStatement());
            //生成相关指令
            //即便是 return function()end 这种也要添加一个var，不然没有地方处理函数

            //存储
            funcInfo.addVar("func",funcInfo);
        }

    }

    /**
     * 返回表达式的值存储的寄存器位置
     */
    public int generateExpr(Expr expr) {
        if (expr instanceof Expr.NameExpr nameExpr) {
            int reg = currentFuncInfo.allocRegister();
            StackElement elem = currentFuncInfo.searchVar(nameExpr.getName());
            if (elem != null) {
                   currentFuncInfo.addCode(new LOADVAR(reg,elem.getIndex()));
                return reg;
            } else {
                UpVal upVal = currentFuncInfo.searchUpVal(nameExpr.getName());
                if (upVal != null) {
                    currentFuncInfo.addCode(new LOADUPVAR(reg,upVal.getIndex()));
                    return  reg;
                }
                int index = FuncInfo.searchGlobalIndex(nameExpr.getName());
                if (index > 0) {
                    currentFuncInfo.addCode(new LOADGLOBAL(reg,index));
                }
                return reg;
            }
        }
        if (expr instanceof Expr.RelExpr rel) {
            int reg1 = generateExpr(rel.getLeft());
            int reg2 = generateExpr(rel.getRight());
            currentFuncInfo.addCode(new EQ(reg1,reg2));
            currentFuncInfo.freeRegister(reg2);
        }
        if (expr instanceof Expr.IndexExpr indexExpr) {
            int reg1 = generateExpr(indexExpr.getLeft());
            int reg2 = generateExpr(indexExpr.getRight());
            currentFuncInfo.addCode(new GETTABLE(reg1,reg2));
        }
        //常量
        if (expr instanceof NumberValue value) {
            int index =FuncInfo.getConstantIndex(value);
            int reg = currentFuncInfo.allocRegister();
            currentFuncInfo.addCode(new LOADCONSTANT(reg,index));
        }
        return -1;
    }
}
