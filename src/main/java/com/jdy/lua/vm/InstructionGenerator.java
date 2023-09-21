package com.jdy.lua.vm;

import com.jdy.lua.data.BoolValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.StringValue;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.jdy.lua.data.NilValue.NIL;
import static com.jdy.lua.statement.Statement.*;
import static com.jdy.lua.vm.ByteCode.*;


/**
 * 用于生成字节码指令
 *
 * @author jdy
 * @title: InstructionGenerator
 * @description:
 * @data 2023/9/18 14:15
 */
public class InstructionGenerator {

    /**
     * 表达式 单个寄存器存储
     */
    private static final int SINGLE = 1;
    /**
     * 表达式 不确定数量寄存器存储
     */
    private static final int UNKNOWN = -1;

    private final FuncInfo funcInfo;
    /**
     * 存储break时 应该跳转的位置
     */
    private final Stack<DynamicLabel> breakLabel = new Stack<>();

    public InstructionGenerator(FuncInfo funcInfo) {
        this.funcInfo = funcInfo;
    }

    public void generateStatement(Statement statement) {
        if (statement instanceof BlockStatement blockStatement) {
            generateBlockStatement(blockStatement);
            return;
        }
        if (statement instanceof LocalFunctionStatement localFunctionStatement) {
            generateLocalFuncStatement(localFunctionStatement);
        }
        if (statement instanceof LocalDefineStatement localDefineStatement) {
            generateLocalAssign(localDefineStatement);
            return;
        }
        if (statement instanceof WhileStatement whileStatement) {
            generateWhile(whileStatement);
            return;
        }
        if (statement instanceof RepeatStatement repeatStatement) {
            generateRepeat(repeatStatement);
            return;
        }
        if (statement instanceof FunctionStatement functionStatement) {
            generateFunc(functionStatement);
        }
        if (statement instanceof IfStatement ifStatement) {
            generateIf(ifStatement);
            return;
        }
        if (statement instanceof LabelStatement labelStatement) {
            generateLabel(labelStatement);
            return;
        }
        if (statement instanceof GotoLabelStatement gotoLabelStatement) {
            generateGoto(gotoLabelStatement);
            return;
        }
        if (statement instanceof BreakStatement) {
            generateBreak();
            return;
        }
        if (statement instanceof ReturnStatement returnStatement) {
            generateReturnStatement(returnStatement);
            return;
        }
        if (statement instanceof AssignStatement assignStatement) {
            generateAssignStatement(assignStatement);
            return;
        }
        if (statement instanceof NumberForStatement numberForStatement) {
            generateNumberFor(numberForStatement);
            return;
        }
        if (statement instanceof GenericForStatement genericfor) {
            generateGenericFor(genericfor);
            return;
        }
        if (statement instanceof RequireModule requireModule) {
            generateModuleStatement(requireModule);
        }
        //不考虑返回值的函数调用
        if (statement instanceof Expr.FuncCallExpr expr) {
            int reg = funcInfo.getUsed();
            generateFuncCallExpr(expr, 0);
            funcInfo.resetRegister(reg);
        }
    }

    /**
     * 执行表达式，并校验最后一个是否是 多返回值的expr
     *
     * @param exprs
     * @param expect 多返回值expr 期望返回值的数量
     */
    private boolean executeExprsCheckMulti(int expect, List<Expr> exprs) {
        int n = exprs.size();
        int i = 0;
        boolean hasMulti = false;
        for (; i < n - 1; i++) {
            generateExpr(exprs.get(i), SINGLE);
        }
        Expr last = exprs.get(n - 1);
        if (hasMultiRet(last)) {
            generateExpr(last, expect);
            hasMulti = true;
        } else {
            generateExpr(last, SINGLE);
        }
        return hasMulti;
    }

    /**
     * 执行 右边的表达式
     * 左边对应 leftNum 个表达式
     */
    private void executeMultiRightExprs(int leftNum, List<Expr> exprs) {
        int n = exprs.size();
        //右边的表达式数量足够，一对一取值
        if (n >= leftNum) {
            exprs.forEach(expr -> generateExpr(expr, SINGLE));
        } else {
            executeExprsCheckMulti(leftNum - n + 1, exprs);
        }
    }

    private boolean setLocalVar(boolean couldGetValue, int valueReg, String varName) {
        StackElement elem = funcInfo.searchVar(varName);
        if (elem != null) {
            if (couldGetValue) {
                funcInfo.addCode(new SAVEVAR(elem.getIndex(), valueReg));
            } else {
                funcInfo.addCode(new SAVENIL(SAVENIL.LOCAL_VAR, elem.getIndex()));
            }
            return true;
        }
        return false;
    }

    private boolean setUpval(boolean couldGetValue, int valueReg, String varName) {
        UpVal upVal = funcInfo.searchUpVal(varName);
        if (upVal != null) {
            if (couldGetValue) {
                funcInfo.addCode(new SAVEUPVAL(upVal.getIndex(), valueReg));
            } else {
                funcInfo.addCode(new SAVENIL(SAVENIL.UPVAL, upVal.getIndex()));
            }
            return true;
        }
        return false;
    }

    private boolean setGlobal(boolean couldGetValue, int valueReg, String varName) {
        GlobalVal globalVal = FuncInfo.searchGlobal(varName);
        if (globalVal != null) {
            if (couldGetValue) {
                funcInfo.addCode(new SAVEGLOBAL(globalVal.index, valueReg));
            } else {
                funcInfo.addCode(new SAVENIL(SAVENIL.GLOBAL, globalVal.getIndex()));
            }
            return true;
        }
        return false;
    }

    private void setTable(boolean couldGetValue, int valueReg, Expr leftExpr, Expr rightExpr) {
        int left = generateExpr(leftExpr, SINGLE);
        int right = generateExpr(rightExpr, SINGLE);
        if (couldGetValue) {
            funcInfo.addCode(new SETTABLE(left, right, valueReg));
        } else {
            funcInfo.addCode(new SETTABLENIL(left, right));
        }
    }

    public void generateNumberFor(NumberForStatement numberForStatement) {
        int curReg = funcInfo.getUsed();
        int init = generateExpr(numberForStatement.getExpr1(), SINGLE);
        //定义变量
        int reg1 = funcInfo.addVar(numberForStatement.getVar(), NIL);
        DynamicLabel endLabel = new DynamicLabel();
        DynamicLabel startLabel = new DynamicLabel();
        breakLabel.add(endLabel);
        //设置初始值
        funcInfo.addCode(new SAVEVAR(reg1, init));
        int reg2 = generateExpr(numberForStatement.getExpr2(), SINGLE);
        int reg3 = generateExpr(numberForStatement.getExpr3() == null ? new NumberValue(1) : numberForStatement.getExpr3(), 1);
        startLabel.setPc(funcInfo.getNextPc());
        funcInfo.addCode(new NUMBERFOR(reg1));
        funcInfo.addCode(new JMP(endLabel));
        generateStatement(numberForStatement.getBlockStatement());
        funcInfo.addCode(new ENDNUMBERFOR(reg1));
        funcInfo.addCode(new JMP(startLabel));
        endLabel.setPc(funcInfo.getNextPc());
        breakLabel.pop();
        funcInfo.resetRegister(curReg);
    }

    public void generateGenericFor(GenericForStatement genericFor) {
        //记录当前寄存器
        final List<String> varNames = genericFor.getVars();
        final List<Integer> varReg = new ArrayList<>();
        //添加变量，获取变量的寄存器位置
        int beforeReg = funcInfo.getUsed();
        varNames.forEach(var -> varReg.add(funcInfo.addVar(var, NilValue.NIL)));
        int afterReg = funcInfo.getUsed();
        // beforeReg + 1, afterReg 之间就是循环变量
        //处理右侧表达式
        //记录使用的寄存器数量
        int regCount = 0;
        for (Expr expr : genericFor.getExpList()) {
            // ipairs, pairs 这种，返回 s,f,var  占用三个寄存器
            if (expr instanceof Expr.FuncCallExpr funcCallExpr) {
                generateExpr(funcCallExpr, 3);
                regCount+=3;
            }  else {
                generateExpr(expr, 1);
                regCount++;
            }
        }
        if (regCount % 3 != 0) {
            throw new RuntimeException("不符合泛型for循环语法");
        }
        DynamicLabel endLabel = new DynamicLabel();
        DynamicLabel startLabel = new DynamicLabel(funcInfo.getNextPc());
        funcInfo.addCode(new GENERICFOR(beforeReg+1,afterReg,afterReg+1,afterReg+regCount));
        funcInfo.addCode(new JMP(endLabel));
        generateStatement(genericFor.getBlockStatement());
        funcInfo.addCode(new JMP(startLabel));
        endLabel.setPc(funcInfo.getNextPc());
        funcInfo.resetRegister(beforeReg);

    }

    public void generateModuleStatement(RequireModule requireModule) {
        //获取模块名的常量下标
        int constantIndex = FuncInfo.getConstantIndex(requireModule.getModuleName());
        //获取加载到的全局变量下标
        int varIndex  = FuncInfo.addGlobalVal(requireModule.getModuleName(), NIL);
        funcInfo.addCode(new LOADGLOBALMODULE(varIndex,constantIndex));
    }


    public void generateAssignStatement(AssignStatement assignStatement) {
        List<Expr> leftExprs = assignStatement.getLeft();
        List<Expr> rightExprs = assignStatement.getRight();
        //调用前寄存器数量
        int beforeReg = funcInfo.getUsed();
        //执行右边的多个表达式
        executeMultiRightExprs(leftExprs.size(), rightExprs);
        int afterReg = funcInfo.getUsed();
        for (int i = 0; i < leftExprs.size(); i++) {
            Expr expr = leftExprs.get(i);
            //获取值所在的寄存器
            int valueReg = i + beforeReg + 1;
            boolean couldGetValue = valueReg <= afterReg;
            if (expr instanceof Expr.NameExpr nameExpr) {
                String name = nameExpr.getName();
                if (!(setLocalVar(couldGetValue, valueReg, name)
                        || setUpval(couldGetValue, valueReg, name) || setGlobal(couldGetValue, valueReg, name))) {
                    //上述三种情况都不是，那么需要新增变量
                    //新增全局变量，默认值是NIL
                    int index = FuncInfo.addGlobalVal(nameExpr.getName(), NIL);
                    if (couldGetValue) {
                        funcInfo.addCode(new SAVEGLOBAL(index, valueReg));
                    }
                }
            } else if (expr instanceof Expr.DotExpr dotExpr) {
                Expr.NameExpr nameExpr = (Expr.NameExpr) dotExpr.getRight();
                setTable(couldGetValue, valueReg, dotExpr.getLeft(), new StringValue(nameExpr.getName()));
            } else if (expr instanceof Expr.IndexExpr indexExpr) {
                setTable(couldGetValue, valueReg, indexExpr.getLeft(), indexExpr.getRight());
            } else {
                throw new RuntimeException("不支持的赋值类型");
            }
        }
        funcInfo.resetRegister(beforeReg);
    }


    public void generateLocalFuncStatement(LocalFunctionStatement localFunctionStatement) {

        FuncInfo localFunc = FuncInfo.createFunc(funcInfo);
        Expr.Function body = localFunctionStatement.getFuncBody();
        localFunc.setHasMultiArg(body.isHasMultiArg());
        localFunc.setParamNames(body.getParamNames());
        for (String param : body.getParamNames()) {
            localFunc.addVar(param, NilValue.NIL);
        }
        //生成相关指令
        InstructionGenerator instructionGenerator = new InstructionGenerator(localFunc);
        int reg = funcInfo.addVar(localFunctionStatement.getFuncName(), NIL);
        int reg2 = funcInfo.allocRegister();
        funcInfo.addCode(new LOADFUNC(reg2, localFunc.getGlobalFuncIndex()));
        //存储
        funcInfo.addCode(new SAVEVAR(reg, reg2));
        funcInfo.resetRegister(reg);

        instructionGenerator.generateStatement(localFunctionStatement.getFuncBody().getBlockStatement());
    }

    public void generateReturnStatement(ReturnStatement returnStatement) {
        if (returnStatement.getExprs().isEmpty()) {
            funcInfo.addCode(new RETURN());
        } else {
            //获取当前的寄存器
            int curReg = funcInfo.getUsed();
            boolean hasMulti = executeExprsCheckMulti(UNKNOWN, returnStatement.getExprs());
            //将范围内的寄存器内容返回
            funcInfo.addCode(new RETURNMULTI(curReg + 1, hasMulti ? UNKNOWN : funcInfo.getUsed()));
        }
    }

    public void generateBlockStatement(BlockStatement blockStatement) {
        //block 中的变量退出后会还原
        int reg = funcInfo.getUsed();
        blockStatement.getStatements().forEach(this::generateStatement);
        funcInfo.resetRegister(reg);
    }


    public void generateLocalAssign(LocalDefineStatement localDefineStatement) {
        final List<String> varNames = localDefineStatement.getVarNames();
        final List<Integer> varReg = new ArrayList<>();
        //添加变量，获取变量的寄存器位置
        varNames.forEach(var -> varReg.add(funcInfo.addVar(var, NilValue.NIL)));
        //记录当前寄存器
        int beforeReg = funcInfo.getUsed();
        //执行右侧的表达式
        executeMultiRightExprs(varNames.size(), localDefineStatement.getExprs());
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
        funcInfo.resetRegister(beforeReg);
    }

    /**
     * 表达式是否回返回多个值
     */
    private boolean hasMultiRet(Expr expr) {
        if (expr instanceof Expr.FuncCallExpr) {
            return true;
        }
        return expr instanceof Expr.MultiArg;
    }

    public void generateRepeat(RepeatStatement repeatStatement) {
        DynamicLabel endLabel = new DynamicLabel();
        DynamicLabel startLabel = new DynamicLabel(funcInfo.getNextPc());
        breakLabel.add(endLabel);
        int curReg = funcInfo.getUsed();
        //执行block的内容
        generateStatement(repeatStatement.getBlockStatement());
        int reg = generateExpr(repeatStatement.getCondition(), SINGLE);
        funcInfo.addCode(new TEST(reg));
        //如果为假，跳到结尾
        funcInfo.addCode(new JMP(endLabel));
        //跳到开始位置
        funcInfo.addCode(new JMP(startLabel));
        //结束
        endLabel.setPc(funcInfo.getNextPc());
        breakLabel.pop();
        //还原寄存器
        funcInfo.resetRegister(curReg);
    }

    public void generateWhile(WhileStatement whileStatement) {
        DynamicLabel endLabel = new DynamicLabel();
        DynamicLabel startLabel = new DynamicLabel(funcInfo.getNextPc());
        breakLabel.add(endLabel);
        int curReg = funcInfo.getUsed();
        int reg = generateExpr(whileStatement.getCondition(), SINGLE);
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
        breakLabel.pop();
        //还原寄存器
        funcInfo.resetRegister(curReg);
    }

    public void generateFunc(FunctionStatement func) {
        //初始化 FuncInfo信息
        FuncInfo newFunc = FuncInfo.createFunc(this.funcInfo);
        Expr.Function body = func.getFuncBody();
        newFunc.setHasMultiArg(body.isHasMultiArg());
        newFunc.setParamNames(body.getParamNames());
        FuncType funcType = func.getFuncName();
        //对象方法添加self 参数
        if (funcType instanceof TableExtendMethod) {
            newFunc.setObjMethod(true);
            newFunc.addVar("self", NilValue.NIL);
        }
        for (String param : body.getParamNames()) {
            newFunc.addVar(param, NilValue.NIL);
        }
        if (body.isHasMultiArg()) {
            newFunc.addVar("...", NIL);
        }
        //存储
        List<String> tableMethodPrefix = new ArrayList<>();
        String funcName = null;
        if (funcType instanceof BasicFuncType name) {
            FuncInfo.addGlobalVal(name.getFuncName(), newFunc);
        } else {
            if (funcType instanceof TableExtendMethod tableExtendMethod) {
                tableMethodPrefix = tableExtendMethod.getFatherTableNames();
                funcName = tableExtendMethod.getMethodName();
            } else if (funcType instanceof TableMethod tableMethod) {
                tableMethodPrefix = tableMethod.getTableNames();
                funcName = tableMethod.getMethodName();
            }
            int beforeReg = funcInfo.getUsed();
            //处理table的方法
            int tableReg = getTable(tableMethodPrefix);
            //将函数名加载到寄存器中
            int indexIndex = FuncInfo.getConstantIndex(funcName);
            int indexReg = funcInfo.allocRegister();
            funcInfo.addCode(new LOADCONSTANT(indexReg, indexIndex));
            int funcReg = funcInfo.allocRegister();
            funcInfo.addCode(new LOADFUNC(funcReg, newFunc.getGlobalFuncIndex()));
            funcInfo.addCode(new SETTABLE(tableReg, indexReg, funcReg));
            //释放占用寄存器
            funcInfo.resetRegister(beforeReg);
        }
        //生成相关指令
        InstructionGenerator instructionGenerator = new InstructionGenerator(newFunc);
        instructionGenerator.generateStatement(func.getFuncBody().getBlockStatement());
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
        for (int i = 1; i < names.size(); i++) {
            int reg2 = funcInfo.allocRegister();
            int index = FuncInfo.getConstantIndex(names.get(i));
            funcInfo.addCode(new LOADCONSTANT(reg2, index));
            funcInfo.addCode(new GETTABLE(reg, reg2));
            funcInfo.freeRegisterWithIndex(reg2);
        }
        return reg;
    }

    public void generateIf(IfStatement ifStatement) {
        int curReg = funcInfo.getUsed();
        DynamicLabel falseLabel = new DynamicLabel();
        DynamicLabel endLabel = new DynamicLabel();
        //处理if
        //获取第一个表达式的值
        int reg1 = generateExpr(ifStatement.getIfCond(), SINGLE);
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
                int reg = generateExpr(elseifConds.get(i), SINGLE);
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
            if (!elseIfFalseLabel.isEmpty()) {
                elseIfFalseLabel.get(elseIfFalseLabel.size() - 1).setPc(funcInfo.getNextPc());
            }
            falseLabel.setPc(funcInfo.getNextPc());
        }
        //设置结尾跳转位置
        endLabel.setPc(funcInfo.getNextPc());
        //重置寄存器
        funcInfo.resetRegister(curReg);
    }

    public void generateBreak() {
        funcInfo.addCode(new JMP(breakLabel.peek()));
    }

    public void generateGoto(GotoLabelStatement gotoLabelStatement) {
        LabelMessage message = funcInfo.getLabel(gotoLabelStatement.getLabelName());
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

        if ((elem = funcInfo.searchVar(expr.getName())) != null) {
            if (elem.isFunc()) {
                funcInfo.addCode(new LOADFUNC(reg, elem.funcIndex()));
            } else {
                funcInfo.addCode(new LOADVAR(reg, elem.getIndex()));
            }
            return reg;
        } else if ((upVal = funcInfo.searchUpVal(expr.getName())) != null) {
            if (upVal.isFunc()) {
                funcInfo.addCode(new LOADFUNC(reg, upVal.funcIndex()));
            } else {
                funcInfo.addCode(new LOADUPVAR(reg, upVal.getIndex()));
            }
            return reg;
        } else if ((globalVal = FuncInfo.searchGlobal(expr.getName())) != null) {
            if (globalVal.isFunc()) {
                funcInfo.addCode(new LOADFUNC(reg, globalVal.getFunGlobalIndex()));
            } else {
                funcInfo.addCode(new LOADGLOBAL(reg, globalVal.getIndex()));
            }
            return reg;
        } else {
            //不存在就将nil 放入寄存器
            funcInfo.addCode(new SAVENIL(SAVENIL.LOCAL_VAR, reg));
        }
        return reg;
    }

    public int generateRelExpr(Expr.RelExpr relExpr) {
        int reg1 = generateExpr(relExpr.getLeft(), SINGLE);
        int reg2 = generateExpr(relExpr.getRight(), SINGLE);
        switch (relExpr.getOp()) {
            case "==" -> funcInfo.addCode(new EQ(reg1, reg2));
            case ">" -> funcInfo.addCode(new GT(reg1, reg2));
            case ">=" -> funcInfo.addCode(new GE(reg1, reg2));
            case "<" -> funcInfo.addCode(new LT(reg1, reg2));
            case "<=" -> funcInfo.addCode(new LE(reg1, reg2));
            case "~=" -> funcInfo.addCode(new NE(reg1, reg2));
        }
        funcInfo.resetRegister(reg1);
        return reg1;
    }


    public int generateUnaryExpr(Expr.UnaryExpr unaryExpr) {
        int reg1 = generateExpr(unaryExpr.getExpr(), SINGLE);
        switch (unaryExpr.getOp()) {
            case "#" -> funcInfo.addCode(new LENGTH(reg1));
            case "not" -> funcInfo.addCode(new NOT(reg1));
            case "-" -> funcInfo.addCode(new SINGLESUB(reg1));
            case "~" -> funcInfo.addCode(new BITREVERSE(reg1));
            default -> throw new RuntimeException("不支持的异常");
        }
        return reg1;
    }

    public int generateCalExpr(Expr.CalExpr calExpr) {
        int reg1 = generateExpr(calExpr.getLeft(), SINGLE);
        int reg2 = generateExpr(calExpr.getRight(), SINGLE);
        switch (calExpr.getOp()) {
            case "+" -> funcInfo.addCode(new ADD(reg1, reg1, reg2));
            case "-" -> funcInfo.addCode(new SUB(reg1, reg1, reg2));
            case "*" -> funcInfo.addCode(new MUL(reg1, reg1, reg2));
            case "/" -> funcInfo.addCode(new DIV(reg1, reg1, reg2));
            case "%" -> funcInfo.addCode(new MOD(reg1, reg1, reg2));
            case "//" -> funcInfo.addCode(new INTMOD(reg1, reg1, reg2));
            case "^" -> funcInfo.addCode(new POW(reg1, reg1, reg2));
            case "&" -> funcInfo.addCode(new BITAND(reg1, reg1, reg2));
            case "|" -> funcInfo.addCode(new BITOR(reg1, reg1, reg2));
            case "<<" -> funcInfo.addCode(new BITLEFTMOVE(reg1, reg1, reg2));
            case ">>" -> funcInfo.addCode(new BITRIGHTMOVE(reg1, reg1, reg2));
            case ".." -> funcInfo.addCode(new CAT(reg1, reg1, reg2));
        }
        funcInfo.resetRegister(reg1);
        return reg1;
    }

    /**
     * 执行后
     * <p>
     * reg1 是函数
     * reg2 是table
     */
    public int generateColonExpr(Expr.ColonExpr colonExpr) {
        int reg1 = generateExpr(colonExpr.getLeft(), SINGLE);
        int reg2 = generateStringValue(new StringValue(colonExpr.getName()));
        funcInfo.addCode(new GETTABLEMETHOD(reg1, reg2));
        return reg1;
    }

    public int generateDotExpr(Expr.DotExpr dotExpr) {
        int reg1 = generateExpr(dotExpr.getLeft(), SINGLE);
        Expr.NameExpr nameExpr = (Expr.NameExpr) dotExpr.getRight();
        int reg2 = generateStringValue(new StringValue(nameExpr.getName()));
        funcInfo.addCode(new GETTABLE(reg1, reg2));
        funcInfo.resetRegister(reg1);
        return reg1;
    }

    public int generateAndExpr(Expr.AndExpr expr) {
        int reg1 = generateExpr(expr.getLeft(), SINGLE);
        int reg2 = generateExpr(expr.getRight(), SINGLE);
        funcInfo.addCode(new AND(reg1, reg1, reg2));
        funcInfo.resetRegister(reg1);
        return reg1;
    }

    public int generateOrExpr(Expr.OrExpr expr) {
        int reg1 = generateExpr(expr.getLeft(), SINGLE);
        int reg2 = generateExpr(expr.getRight(), SINGLE);
        funcInfo.addCode(new OR(reg1, reg1, reg2));
        funcInfo.resetRegister(reg1);
        return reg1;
    }


    public int generateFuncExpr(Expr.Function function) {
        FuncInfo func = FuncInfo.createFunc(funcInfo);
        func.setHasMultiArg(function.isHasMultiArg());
        func.setParamNames(function.getParamNames());
        function.getParamNames().forEach(param -> {
            func.addVar(param, NilValue.NIL);
        });
        if (function.isHasMultiArg()) {
            func.addVar("...", NilValue.NIL);
        }
        InstructionGenerator instructionGenerator = new InstructionGenerator(func);
        int reg = funcInfo.allocRegister();
        //将函数加载到寄存器中
        funcInfo.addCode(new LOADFUNC(reg, func.getGlobalFuncIndex()));
        instructionGenerator.generateStatement(function.getBlockStatement());
        return reg;
    }

    public int generateTableExpr(Expr.TableExpr tableExpr) {
        //reg1存放table
        int reg1 = funcInfo.allocRegister();
        funcInfo.addCode(new NEWTABLE(reg1));
        tableExpr.getExprExprMap().forEach((key, value) -> {
            //当成常量去处理
            int reg2;
            if (key instanceof Expr.NameExpr nameExpr) {
                reg2 = generateStringValue(new StringValue(nameExpr.getName()));
            } else {
                reg2 = generateExpr(key, SINGLE);
            }
            int reg3 = generateExpr(value, SINGLE);
            funcInfo.addCode(new SETTABLE(reg1, reg2, reg3));
            funcInfo.resetRegister(reg1);
        });
        return reg1;
    }

    public int generateNilExpr(NilValue nilValue) {
        int index = FuncInfo.getConstantIndex(nilValue);
        int reg = funcInfo.allocRegister();
        funcInfo.addCode(new LOADCONSTANT(reg, index));
        return reg;
    }

    public int generateBooleanExpr(BoolValue boolValue) {
        int index = FuncInfo.getConstantIndex(boolValue);
        int reg = funcInfo.allocRegister();
        funcInfo.addCode(new LOADCONSTANT(reg, index));
        return reg;
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
        int reg1 = generateExpr(indexExpr.getLeft(), SINGLE);
        int reg2 = generateExpr(indexExpr.getRight(), SINGLE);
        funcInfo.addCode(new GETTABLE(reg1, reg2));
        return reg1;
    }

    public int generateFuncCallExpr(Expr.FuncCallExpr funcCallExpr, int expect) {
        //加载函数存放到寄存器 reg1
        int reg1 = generateExpr(funcCallExpr.getFunc(), SINGLE);
        List<Expr> args = funcCallExpr.getExprs();
        //处理函数参数
        boolean hasMultiRet = false;
        if (!args.isEmpty()) {
            hasMultiRet = executeExprsCheckMulti(UNKNOWN, args);
        }
        funcInfo.addCode(new CALL(reg1, reg1 + 1, hasMultiRet ? UNKNOWN : reg1 + args.size(), expect));
        //释放多余寄存器，从 reg1 开始 只需要留有 expect个就行
        if (expect != -1) {
            funcInfo.resetRegister(reg1 + expect - 1);
        }
        return reg1;
    }

    public int generateMultiArg(int expect) {
        int a = funcInfo.allocRegister();
        funcInfo.addCode(new VARARGS(a, expect));
        return a;
    }

    public int generateModule(RequireModule requireModule) {
        int reg = funcInfo.allocRegister();
        int constantIndex = FuncInfo.getConstantIndex(requireModule.getModuleName());
        funcInfo.addCode(new LOADMODULE(reg,constantIndex));
        return reg;
    }

    /**
     * 返回表达式的值存储的寄存器位置
     *
     * @param expect 期望表达式的值，占用了几个寄存器
     */
    public int generateExpr(Expr expr, int expect) {
        if (expr instanceof NilValue nilValue) {
            return generateNilExpr(nilValue);
        } else if (expr instanceof BoolValue boolValue) {
            return generateBooleanExpr(boolValue);
        } else if (expr instanceof Expr.IndexExpr indexExpr) {
            return generateIndexExpr(indexExpr);
        } else if (expr instanceof StringValue stringValue) {
            return generateStringValue(stringValue);
        } else if (expr instanceof NumberValue numberValue) {
            return generateNumberExpr(numberValue);
        } else if (expr instanceof Expr.TableExpr tableExpr) {
            return generateTableExpr(tableExpr);
        } else if (expr instanceof Expr.Function function) {
            return generateFuncExpr(function);
        } else if (expr instanceof Expr.CalExpr calExpr) {
            return generateCalExpr(calExpr);
        } else if (expr instanceof Expr.RelExpr relExpr) {
            return generateRelExpr(relExpr);
        } else if (expr instanceof Expr.NameExpr nameExpr) {
            return generateNameExpr(nameExpr);
        } else if (expr instanceof Expr.AndExpr andExpr) {
            return generateAndExpr(andExpr);
        } else if (expr instanceof Expr.OrExpr orExpr) {
            return generateOrExpr(orExpr);
        } else if (expr instanceof Expr.DotExpr dotExpr) {
            return generateDotExpr(dotExpr);
        } else if (expr instanceof Expr.FuncCallExpr funcCallExpr) {
            return generateFuncCallExpr(funcCallExpr, expect);
        } else if (expr instanceof Expr.ColonExpr colonExpr) {
            return generateColonExpr(colonExpr);
        } else if (expr instanceof Expr.MultiArg) {
            return generateMultiArg(expect);
        } else if (expr instanceof Expr.UnaryExpr unaryExpr) {
            return generateUnaryExpr(unaryExpr);
        } else if (expr instanceof RequireModule requireModule) {
            return generateModule(requireModule);
        }
        return 0;
    }
}
