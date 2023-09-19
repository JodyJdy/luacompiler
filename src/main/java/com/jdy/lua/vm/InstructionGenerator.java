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
        if (statement instanceof BreakStatement breakStatement) {
            generateBreak(breakStatement);
            return;
        }
        if (statement instanceof ContinueStatement continueStatement) {
            generateContinue(continueStatement);
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
        //不考虑返回值的函数调用
        if (statement instanceof Expr.FuncCallExpr expr) {
            int reg = funcInfo.getUsed();
            generateFuncCallExpr(expr);
            funcInfo.resetRegister(reg);
        }
    }

    public void generateAssignStatement(AssignStatement assignStatement) {
        List<Expr> exprs = assignStatement.getLeft();
        //调用前寄存器数量
        int beforeReg = funcInfo.getUsed();
        assignStatement.getRight().forEach(this::generateExpr);
        int afterReg = funcInfo.getUsed();
        for (int i = 0; i < exprs.size(); i++) {
            Expr expr = exprs.get(i);
            //获取值所在的寄存器
            int value = i + beforeReg + 1;
            if (expr instanceof Expr.NameExpr nameExpr) {
                StackElement elem;
                UpVal upVal;
                GlobalVal globalVal;
                if ((elem = funcInfo.searchVar(nameExpr.getName())) != null) {
                    if (value <= afterReg) {
                        funcInfo.addCode(new SAVEVAR(elem.getIndex(), value));
                    } else{
                        funcInfo.addCode(new SAVENIL(0,elem.getIndex()));
                    }
                } else if ((upVal = funcInfo.searchUpVal(nameExpr.getName())) != null) {
                    if (value <= afterReg) {
                        funcInfo.addCode(new SAVEUPVAL(upVal.getIndex(), value));
                    } else{
                        funcInfo.addCode(new SAVENIL(1,upVal.getIndex()));
                    }
                } else if ((globalVal = FuncInfo.searchGlobal(nameExpr.getName())) != null) {
                    if (value <= afterReg) {
                        funcInfo.addCode(new SAVEGLOBAL(globalVal.index, value));
                    } else{
                        funcInfo.addCode(new SAVENIL(2,globalVal.getIndex()));
                    }
                } else{
                    int index =FuncInfo.addGlobalVal(nameExpr.getName(), NIL);
                    //新增全局变量
                    if (value <= afterReg) {
                        funcInfo.addCode(new SAVEGLOBAL(index, value));
                    }
                    //什么也不做，因为变量已经是nil了
                }
            } else if (expr instanceof Expr.DotExpr dotExpr) {
                    int left =generateExpr(dotExpr.getLeft());
                    Expr.NameExpr nameExpr = (Expr.NameExpr) dotExpr.getRight();
                    //转成常量
                    int right = generateExpr(new StringValue(nameExpr.getName()));
                if (value <= afterReg) {
                    funcInfo.addCode(new SETTABLE(left,right,value));
                } else{
                    funcInfo.addCode(new SETTABLENIL(left,right));
                }

            } else if (expr instanceof Expr.IndexExpr indexExpr) {
                int left =generateExpr(indexExpr.getLeft());
                int right = generateExpr(indexExpr.getRight());
                if (value <= afterReg) {
                    funcInfo.addCode(new SETTABLE(left,right,value));
                } else{
                    funcInfo.addCode(new SETTABLENIL(left,right));
                }
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
        instructionGenerator.generateStatement(localFunctionStatement.getFuncBody().getBlockStatement());
        int reg = funcInfo.addVar(localFunctionStatement.getFuncName(), NIL);
        //存储
        funcInfo.addCode(new SAVEVAR(reg, funcInfo.getGlobalFuncIndex(), true));
        localFunc.fillJMP();
    }

    public void generateReturnStatement(ReturnStatement returnStatement) {
        if (returnStatement.getExprs().isEmpty()) {
            funcInfo.addCode(new RETURN());
        } else {
            //获取当前的寄存器
            int curReg = funcInfo.getUsed();
            returnStatement.getExprs().forEach(this::generateExpr);
            //将范围内的寄存器内容返回
            funcInfo.addCode(new RETURNMULTI(curReg + 1, funcInfo.getUsed()));
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
        funcInfo.resetRegister(beforeReg);
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
        //生成相关指令
        InstructionGenerator instructionGenerator = new InstructionGenerator(newFunc);
        instructionGenerator.generateStatement(func.getFuncBody().getBlockStatement());
        //存储
        List<String> tableMethodPrefix = new ArrayList<>();
        String funcName = null;
        if (funcType instanceof BasicFuncType name) {
            FuncInfo.addGlobalVal(name.getFuncName(), newFunc);
            return;
        } else if (funcType instanceof TableExtendMethod tableExtendMethod) {
            tableMethodPrefix = tableExtendMethod.getFatherTableNames();
            funcName = tableExtendMethod.getMethodName();
        } else if (funcType instanceof TableMethod tableMethod) {
            tableMethodPrefix = tableMethod.getTableNames();
            funcName = tableMethod.getMethodName();
        }
        //处理table的方法
        int tableReg = getTable(tableMethodPrefix);
        //将函数名加载到寄存器中
        int indexIndex = FuncInfo.getConstantIndex(funcName);
        int indexReg = funcInfo.allocRegister();
        funcInfo.addCode(new LOADCONSTANT(indexReg, indexIndex));
        funcInfo.addCode(new SETTABLE(tableReg, indexReg, funcInfo.getGlobalFuncIndex(), true));
        //释放占用的三个寄存器
        funcInfo.freeRegister(3);
        newFunc.fillJMP();
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
            funcInfo.addCode(new LOADVAR(reg, elem.getIndex()));
            return reg;
        } else if ((upVal = funcInfo.searchUpVal(expr.getName())) != null) {
            funcInfo.addCode(new LOADUPVAR(reg, upVal.getIndex()));
            return reg;
        } else if ((globalVal = FuncInfo.searchGlobal(expr.getName())) != null) {
            funcInfo.addCode(new LOADGLOBAL(reg, globalVal.getIndex()));
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
        funcInfo.resetRegister(reg1);
        return reg1;
    }

    public int generateCalExpr(Expr.CalExpr calExpr) {
        int reg1 = generateExpr(calExpr.getLeft());
        int reg2 = generateExpr(calExpr.getRight());
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
     *
     * reg1 是函数
     * reg2 是table
     *
     */
    public int generateColonExpr(Expr.ColonExpr colonExpr) {
        int reg1 = generateExpr(colonExpr.getLeft());
        int reg2 = generateStringValue(new StringValue(colonExpr.getName()));
        funcInfo.addCode(new GETTABLEMETHOD(reg1, reg2));
        return reg1;
    }

    public int generateDotExpr(Expr.DotExpr dotExpr) {
        int reg1 = generateExpr(dotExpr.getLeft());
        int reg2 = generateExpr(dotExpr.getRight());
        funcInfo.addCode(new GETTABLE(reg1, reg2));
        funcInfo.freeRegisterWithIndex(reg2);
        return reg1;
    }

    public int generateAndExpr(Expr.AndExpr expr) {
        int reg1 = generateExpr(expr.getLeft());
        int reg2 = generateExpr(expr.getRight());
        funcInfo.addCode(new AND(reg1, reg1, reg2));
        funcInfo.freeRegisterWithIndex(reg2);
        return reg1;
    }

    public int generateOrExpr(Expr.OrExpr expr) {
        int reg1 = generateExpr(expr.getLeft());
        int reg2 = generateExpr(expr.getRight());
        funcInfo.addCode(new OR(reg1, reg1, reg2));
        funcInfo.freeRegisterWithIndex(reg2);
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
        instructionGenerator.generateStatement(function.getBlockStatement());
        int reg = funcInfo.allocRegister();
        //将函数加载到寄存器中
        funcInfo.addCode(new LOADFUNC(reg, func.getGlobalFuncIndex()));
        func.fillJMP();
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
                reg2 = generateExpr(key);
            }
            int reg3 = generateExpr(value);
            funcInfo.addCode(new SETTABLE(reg1, reg2, reg3));
            funcInfo.resetRegister(reg1);
        });
        return 0;
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
        int reg1 = generateExpr(indexExpr.getLeft());
        int reg2 = generateExpr(indexExpr.getRight());
        funcInfo.addCode(new GETTABLE(reg1, reg2));
        return reg1;
    }

    public int generateFuncCallExpr(Expr.FuncCallExpr funcCallExpr) {
        //获取函数所在的寄存器
        int reg1 = generateExpr(funcCallExpr.getFunc());
        //处理函数参数
        funcCallExpr.getExprs().forEach(this::generateExpr);
        funcInfo.addCode(new CALL(reg1, reg1 + 1, funcInfo.getUsed()));
        //调用函数，返回值，从reg1开始放置，寄存器的调整，由虚拟机实现
        return reg1;
    }


    /**
     * 返回表达式的值存储的寄存器位置
     */
    public int generateExpr(Expr expr) {
        if (expr instanceof NilValue nilValue) {
            return generateNilExpr(nilValue);
        }
        if (expr instanceof BoolValue boolValue) {
            return generateBooleanExpr(boolValue);
        }
        if (expr instanceof Expr.IndexExpr indexExpr) {
            return generateIndexExpr(indexExpr);
        }
        if (expr instanceof StringValue stringValue) {
            return generateStringValue(stringValue);
        }
        if (expr instanceof NumberValue numberValue) {
            return generateNumberExpr(numberValue);
        }
        if (expr instanceof Expr.TableExpr tableExpr) {
            return generateTableExpr(tableExpr);
        }
        if (expr instanceof Expr.Function function) {
            return generateFuncExpr(function);
        }
        if (expr instanceof Expr.CalExpr calExpr) {
            return generateCalExpr(calExpr);
        }
        if (expr instanceof Expr.RelExpr relExpr) {
            return generateRelExpr(relExpr);
        }
        if (expr instanceof Expr.NameExpr nameExpr) {
            return generateNameExpr(nameExpr);
        }
        if (expr instanceof Expr.AndExpr andExpr) {
            return generateAndExpr(andExpr);
        }
        if (expr instanceof Expr.OrExpr orExpr) {
            return generateOrExpr(orExpr);
        }
        if (expr instanceof Expr.DotExpr dotExpr) {
            return generateDotExpr(dotExpr);
        }
        if (expr instanceof Expr.FuncCallExpr funcCallExpr) {
            return generateFuncCallExpr(funcCallExpr);
        }
        if (expr instanceof Expr.ColonExpr colonExpr) {
            return generateColonExpr(colonExpr);
        }
        return 0;
    }
}
