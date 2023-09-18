package com.jdy.lua.vm;

import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.StringValue;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.jdy.lua.vm.ByteCode.*;
import static com.jdy.lua.statement.Statement.*;


/**
 * 用于生成字节码指令
 *
 * @author jdy
 * @title: InstructionGenerator
 * @description:
 * @data 2023/9/18 14:15
 */
public class InstructionGenerator {

    private final FuncInfo funcInfo;
    /**
     * 存储continue时 应该跳转的位置
     */
    private final Stack<DynamicLabel> continueLabel = new Stack<>();
    /**
     * 存储break时 应该跳转的位置
     */
    private final Stack<DynamicLabel> breakLabel = new Stack<>();

    public InstructionGenerator(FuncInfo funcInfo) {
        this.funcInfo = funcInfo;
    }

    public void generateStatement(Statement statement) {
    }


    public void generateLocalAssign(LocalDefineStatement localDefineStatement) {
        final List<String> varNames = localDefineStatement.getVarNames();
        final List<Integer> varReg = new ArrayList<>();
        //添加变量，获取变量的寄存器位置
        varNames.forEach(var -> varReg.add(funcInfo.addVar(var, NilValue.NIL)));
        //记录当前寄存器
        int beforeReg = funcInfo.getUsed();
        //执行后面的表达式
        localDefineStatement.getExprs().forEach(this::generateExpr);
        int afterReg = funcInfo.getUsed();
        //进行赋值
        for (int i = 0; i < varReg.size(); i++) {
            //变量i对应的值的寄存器位置
            int valueReg = beforeReg + i + 1;
            if (valueReg <= afterReg) {
                funcInfo.addCode(new SAVEVAR(varReg.get(i), valueReg));
            } else {
                break;
            }
        }
    }

    public void generateWhile(WhileStatement whileStatement) {
        DynamicLabel endLabel = new DynamicLabel();
        DynamicLabel startLabel = new DynamicLabel(funcInfo.getNextPc());
        breakLabel.add(endLabel);
        continueLabel.add(startLabel);
        int curReg = funcInfo.getUsed();
        int reg = generateExpr(whileStatement.getCondition());
        funcInfo.addCode(new TEST(reg));
        //如果为假，跳到结尾
        funcInfo.addCode(new JMP(endLabel));
        //执行block的内容
        funcInfo.freeRegisterWithIndex(reg);
        generateStatement(whileStatement.getBlockStatement());
        //跳到开始位置
        funcInfo.addCode(new JMP(startLabel));
        //结束
        endLabel.setPc(funcInfo.getNextPc());
        //还原寄存器
        funcInfo.resetRegister(curReg);
    }

    public void generateFunc(FunctionStatement func) {
        //初始化 FuncInfo信息
        FuncInfo newFunc = new FuncInfo(this.funcInfo);
        Expr.Function body = func.getFuncBody();
        newFunc.setHasMultiArg(body.isHasMultiArg());
        newFunc.setParamNames(body.getParamNames());
        for (String param : body.getParamNames()) {
            newFunc.addVar(param, NilValue.NIL);
        }
        //生成相关指令
        InstructionGenerator instructionGenerator = new InstructionGenerator(newFunc);
        instructionGenerator.generateStatement(func.getFuncBody().getBlockStatement());
        //存储
        FuncType funcType = func.getFuncName();
        List<String> tableMethodPrefix = new ArrayList<>();
        String funcName = null;
        if (funcType instanceof BasicFuncType name) {
            FuncInfo.addGlobalVal(name.getFuncName(), newFunc);
            return;
        } else if (funcType instanceof TableExtendMethod tableExtendMethod) {
            newFunc.setObjMethod(true);
            tableMethodPrefix = tableExtendMethod.getFatherTableNames();
            funcName = tableExtendMethod.getMethodName();
        } else if (funcType instanceof TableMethod tableMethod) {
            tableMethodPrefix = tableMethod.getTableNames();
            funcName = tableMethod.getMethodName();
        }
        //处理table的方法
        int tableReg = getTable(tableMethodPrefix);
        int indexIndex = FuncInfo.getConstantIndex(funcName);
        int indexReg = newFunc.allocRegister();
        newFunc.addCode(new LOADCONSTANT(indexReg, indexIndex));
        //用于存放函数的寄存器
        int funReg = newFunc.allocRegister();
        //将函数放入
        funcInfo.setRegisterVal(funReg, newFunc);
        funcInfo.addCode(new SETTABLE(tableReg, indexReg, funReg));
        //释放占用的三个寄存器
        funcInfo.freeRegister(3);
    }


    public int getTable(List<String> names) {
        int reg = funcInfo.allocRegister();
        StackElement local;
        UpVal upVal;
        GlobalVal globalVal;
        String table = names.get(0);
        if ((local = funcInfo.searchVar(table)) != null) {
            funcInfo.addCode(new LOADVAR(reg, local.getIndex()));
        } else if ((upVal = funcInfo.searchUpVal(table)) != null) {
            funcInfo.addCode(new LOADUPVAR(reg, upVal.getIndex()));
        } else if ((globalVal = FuncInfo.searchGlobal(table)) != null) {
            funcInfo.addCode(new LOADGLOBAL(reg, globalVal.getIndex()));
        } else {
            throw new RuntimeException(String.format("table:%s不存在", table));
        }
        int reg2 = funcInfo.allocRegister();
        for (int i = 1; i < names.size(); i++) {
            int index = FuncInfo.getConstantIndex(names.get(i));
            funcInfo.addCode(new LOADCONSTANT(reg2, index));
            funcInfo.addCode(new GETTABLE(reg, reg2));
            funcInfo.freeRegister(reg2);
        }
        return reg;
    }

    public void generateIf(IfStatement ifStatement) {
        int curReg = funcInfo.getUsed();
        DynamicLabel falseLabel = new DynamicLabel();
        DynamicLabel endLabel = new DynamicLabel();
        //处理if
        //获取第一个表达式的值
        int reg1 = generateExpr(ifStatement.getIfCond());
        funcInfo.addCode(new TEST(reg1));
        //跳到假出口
        funcInfo.addCode(new JMP(falseLabel));
        // ifCond为真 执行
        funcInfo.freeRegisterWithIndex(reg1);
        generateStatement(ifStatement.getIfBlock());
        //跳到结尾位置
        funcInfo.addCode(new JMP(endLabel));

        List<DynamicLabel> elseIfFalseLabel = new ArrayList<>();
        if (!ifStatement.getElseIfBlocks().isEmpty()) {
            List<Expr> elseifConds = ifStatement.getElseIfConds();
            List<BlockStatement> elseIfBlock = ifStatement.getElseIfBlocks();
            //回填if的假出口
            falseLabel.setPc(funcInfo.getNextPc());
            for (int i = 0; i < elseifConds.size(); i++) {
                //回填上一个的假出口
                if (i > 0) {
                    elseIfFalseLabel.get(i - 1).setPc(funcInfo.getNextPc());
                }
                DynamicLabel tempFalseLabel = new DynamicLabel();
                elseIfFalseLabel.add(tempFalseLabel);
                int reg = generateExpr(elseifConds.get(i));
                funcInfo.addCode(new TEST(reg));
                //跳到假出口
                funcInfo.addCode(new JMP(tempFalseLabel));
                // Cond为真 执行
                funcInfo.freeRegisterWithIndex(reg);
                generateStatement(elseIfBlock.get(i));
                //跳到结尾位置
                funcInfo.addCode(new JMP(endLabel));
            }
        }
        if (ifStatement.getElseBlock() != null) {
            //处理else
            //没有else-if，直接跳转到此处的位置
            if (elseIfFalseLabel.isEmpty()) {
                falseLabel.setPc(funcInfo.getNextPc());
            } else {
                elseIfFalseLabel.get(elseIfFalseLabel.size() - 1).setPc(funcInfo.getNextPc());
            }
            generateStatement(ifStatement.getElseBlock());
        } else {
            falseLabel.setPc(funcInfo.getNextPc());
        }
        //设置结尾跳转位置
        endLabel.setPc(funcInfo.getNextPc());
        //重置寄存器
        funcInfo.resetRegister(curReg);
    }

    public void generateBreak(BreakStatement breakStatement) {
        funcInfo.addCode(new JMP(breakLabel.peek()));
    }

    public void generateContinue(ContinueStatement continueStatement) {
        funcInfo.addCode(new JMP(continueLabel.peek()));
    }

    public void generateGoto(LabelStatement labelStatement) {
        LabelMessage message = funcInfo.getLabel(labelStatement.getLabelName());
        funcInfo.addCode(new JMP(message.getPc()));
        funcInfo.resetRegister(message.getUsedReg());
    }

    public void generateLabel(LabelStatement labelStatement) {
        funcInfo.addLabel(labelStatement.getLabelName());
    }

    public int generateNameExpr(Expr.NameExpr expr) {
            int reg = funcInfo.allocRegister();
            StackElement elem;
            UpVal upVal;
            GlobalVal globalVal;

            if ((elem = funcInfo.searchVar(expr.getName()))!= null) {
                funcInfo.addCode(new LOADVAR(reg, elem.getIndex()));
                return reg;
            } else if((upVal = funcInfo.searchUpVal(expr.getName())) != null){
                    funcInfo.addCode(new LOADUPVAR(reg, upVal.getIndex()));
                    return reg;
            } else if((globalVal=FuncInfo.searchGlobal(expr.getName()))!=null){
                int index = globalVal.index;
                if (index > 0) {
                    funcInfo.addCode(new LOADGLOBAL(reg, index));
                }
                return reg;
            }
        throw new RuntimeException(String.format("变量:%s不存在", expr.getName()));
    }

    public int generateRelExpr(Expr.RelExpr relExpr) {
        int reg1 = generateExpr(relExpr.getLeft());
        int reg2 = generateExpr(relExpr.getRight());
        switch (relExpr.getOp()) {
            case "==" -> funcInfo.addCode(new EQ(reg1, reg2));
            case ">" -> funcInfo.addCode(new GT(reg1, reg2));
            case ">=" -> funcInfo.addCode(new GE(reg1, reg2));
            case "<" -> funcInfo.addCode(new LT(reg1, reg2));
            case "<=" -> funcInfo.addCode(new LE(reg1, reg2));
            case "~=" -> funcInfo.addCode(new NE(reg1, reg2));
        }
        funcInfo.freeRegister(reg2);
        return reg1;
    }

    public int generateCalExpr(Expr.CalExpr calExpr) {
        int reg1 = generateExpr(calExpr.getLeft());
        int reg2 = generateExpr(calExpr.getRight());
        switch (calExpr.getOp()) {
            case "+" -> funcInfo.addCode(new ADD(reg1,reg1,reg2));
            case "-" ->funcInfo.addCode(new SUB(reg1,reg1,reg2));
            case "*" ->funcInfo.addCode(new MUL(reg1,reg1,reg2));
            case "/" ->funcInfo.addCode(new DIV(reg1,reg1,reg2));
            case "%" ->funcInfo.addCode(new MOD(reg1,reg1,reg2));
            case "//" ->funcInfo.addCode(new INTMOD(reg1,reg1,reg2));
            case "^" ->funcInfo.addCode(new POW(reg1,reg1,reg2));
            case "&" ->funcInfo.addCode(new BITAND(reg1,reg1,reg2));
            case "|" ->funcInfo.addCode(new BITOR(reg1,reg1,reg2));
            case "<<" ->funcInfo.addCode(new BITLEFTMOVE(reg1,reg1,reg2));
            case ">>" ->funcInfo.addCode(new BITRIGHTMOVE(reg1,reg1,reg2));
            case ".." ->funcInfo.addCode(new CAT(reg1,reg1,reg2));
        }
        funcInfo.freeRegister(reg2);
        return reg1;
    }

    public int generateTableExpr(Expr.TableExpr tableExpr) {
        return 0;
    }

    public int generateNumberExpr(NumberValue numberValue) {
        int index = FuncInfo.getConstantIndex(numberValue);
        int reg = funcInfo.allocRegister();
        funcInfo.addCode(new LOADCONSTANT(reg, index));
        return reg;
    }

    public int generateStringValue(StringValue stringValue) {
        int index = FuncInfo.getConstantIndex(stringValue);
        int reg = funcInfo.allocRegister();
        funcInfo.addCode(new LOADCONSTANT(reg, index));
        return reg;
    }

    public int generateIndexExpr(Expr.IndexExpr indexExpr) {
        int reg1 = generateExpr(indexExpr.getLeft());
        int reg2 = generateExpr(indexExpr.getRight());
        funcInfo.addCode(new GETTABLE(reg1, reg2));
        return reg1;
    }
    /**
     * 返回表达式的值存储的寄存器位置
     */
    public int generateExpr(Expr expr) {
        return 0;
    }
}
