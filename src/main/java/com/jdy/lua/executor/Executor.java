package com.jdy.lua.executor;

import com.jdy.lua.data.*;
import com.jdy.lua.data.Function;
import com.jdy.lua.luanative.NativeFunction;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;

import java.util.*;

import static com.jdy.lua.statement.Statement.*;
import static com.jdy.lua.statement.Expr.*;
import static com.jdy.lua.data.BoolValue.*;
import static com.jdy.lua.data.NilValue.*;
import static com.jdy.lua.executor.Checker.*;

/**
 * @author jdy
 * @title: Executor
 * @description:
 * @data 2023/9/15 16:09
 */
public class Executor {

    private Block currentBlock;

    /**
     * 存储label的坐标
     */
    private final Map<String, LabelLocation> labelLocation = new HashMap<>();

    /**
     * 当前的block的层级
     */
    private int blockLevel = 0;
    /**
     * 执行了 Goto
     */
    private String gotoLabel;
    /**
     * 返回值，lua可以有多返回值
     */
    protected Value returnValue = NIL;

    /**
     * 是否执行了返回
     */
    private boolean returned = false;

    /**
     * loop statement's level
     */
    private int circleLevel = 0;

    /**
     * break statement's circle level
     */
    private int breakLevel = CLEAR_MARK;
    /**
     * continue statement's circle level
     */
    private int continueLevel = CLEAR_MARK;

    /**
     * check mark
     */
    private boolean shouldReturn() {
        return returned;
    }

    private boolean shouldContinue() {
        return inCircle() && continueLevel == circleLevel;
    }

    private boolean shouldBreak() {
        return inCircle() && breakLevel == circleLevel;
    }

    /**
     * 清除 break，continue标记
     */
    private static final int CLEAR_MARK = -1;

    private boolean inCircle() {
        return circleLevel != 0;
    }

    /**
     * 函数调用时的参数
     */
    private List<Value> args;

    /**
     * 执行block
     * 入参为 block
     */
    public Executor(BlockStatement blockStatement) {
        this.blockStatement = blockStatement;
    }

    /**
     * 执行函数
     */
    public Executor(com.jdy.lua.data.Function func, List<Value> args) {
        this.function = func;
        if (!(function instanceof NativeFunction)) {
            this.blockStatement = func.getBody().getBlockStatement();
        }
        this.args = args;
    }

    private Value execute(Block parent) {
        Block block = new Block(blockStatement);
        block.setParent(parent);
        currentBlock = block;
        //如果是函数调用，会准备参数
        prepareArg();
        executeBlock(block);
        return returnValue;
    }

    public Value execute() {
        //如果执行的是Native函数
        if (function != null && function.isNative()) {
            NativeFunction nativeFunction = (NativeFunction) function;
            return nativeFunction.execute(args);
        }
        if (function != null) {
            return execute(function.getParent());
        }
        return execute(null);
    }


    /**
     * 准备实参
     */
    private void prepareArg() {
        if (this.args == null) {
            return;
        }
        Expr.Function functionBody = function.getBody();
        List<String> parameterNames = functionBody.getParamNames();
        int i = 0;
        //包含self默认参数，第一个就是默认参数
        if (function.isObjMethod()) {
            currentBlock.addVar("self",args.get(i));
        }
        for (; i < parameterNames.size(); i++) {
            if (i < args.size()) {
                currentBlock.addVar(parameterNames.get(i), args.get(i));
            } else {
                currentBlock.addVar(parameterNames.get(i), NIL);
            }
        }
        //处理变长参数
        if (functionBody.isHasMultiArg()) {
            if (i < args.size()) {
                currentBlock.addVar("...", new MultiValue(args.subList(i, args.size())));
            }
        }
    }

    private BlockStatement blockStatement;
    private Function function;


    /**
     * 如果跳转的位置是当前label则，继续执行，返回执行的下标
     */
    private  int checkStatementIndex(){
        //如果 发生了goto，且跳转的位置是当前block，则继续
        if (gotoLabel != null) {
            LabelLocation location = labelLocation.get(gotoLabel);
            //应该在当前block执行
            if (location.getBlockLevel() == blockLevel) {
                //重置 gotoLabel 标记
                gotoLabel = null;
                return location.getStatementIndex();
            }
        }
        return  -1;
    }

    public void executeBlock(Block block) {
        List<Statement> statementList = block.getBlockStatement().getStatements();
        for (int i = 0; i < statementList.size(); i++) {
            Statement stat = statementList.get(i);
            //记录label的位置
            if (stat instanceof LabelStatement) {
                String label = ((LabelStatement) stat).getLabelName();
                labelLocation.put(label, new LabelLocation(blockLevel, i));
            } else {
                statementList.get(i).visitStatement(this);
            }
            //block是否应该结束
            if (blockShouldStop()) {
                if ((i = checkStatementIndex()) >= 0){
                } else {
                    break;
                }
            }
        }
    }

    public void executeStatement(Statement statement) {
        System.out.println("??????????????");
        System.out.println(statement.getClass());
    }


    /**
     * block
     * 是否应该停止运行：
     * <p>
     * 发生了 return,goto, continue, break时
     */
    public boolean blockShouldStop() {
        //执行了 return 语句
        if (returned) {
            return true;
        }
        //需要跳转
        if (gotoLabel != null) {
            return true;
        }
        if (inCircle()) {
            return shouldBreak() || shouldContinue();
        }
        return false;
    }

    /**
     * 变量定义
     */
    public void executeStatement(LocalDefineStatement local) {
        List<String> vars = local.getVarNames();
        List<Expr> exprs = local.getExprs();
        List<Value> valueList = new ArrayList<>();
        for (Expr expr : exprs) {
            Value val = expr.visitExpr(this);
            if (val instanceof MultiValue multiValue) {
                valueList.addAll(multiValue.getValueList());
            } else {
                valueList.add(val);
            }
        }
        for (int i = 0; i < vars.size(); i++) {
            if (i < valueList.size()) {
                currentBlock.addVar(vars.get(i), valueList.get(i));
            } else {
                currentBlock.addVar(vars.get(i), NilValue.NIL);
            }
        }
    }

    /**
     * 一共只会有三种
     * 1.
     * a = xx
     * <p>
     * 2.
     * a.b = xx
     * <p>
     * 3.
     * a[b] =xx
     */
    public void executeStatement(AssignStatement assign) {
        List<Expr> exprs = assign.getLeft();
        List<Value> initValues = new ArrayList<>();
        assign.getRight().forEach(e -> {
            Value value = e.visitExpr(this);
            //多返回值，进行展开
            if (value instanceof MultiValue multiValue) {
                initValues.addAll(multiValue.getValueList());
            } else {
                initValues.add(e.visitExpr(this));
            }
        });
        for (int i = 0; i < exprs.size(); i++) {
            Expr expr = exprs.get(i);
            Value value = i < initValues.size() ? initValues.get(i) : NIL;
            if (expr instanceof NameExpr nameExpr) {
                Variable variable = currentBlock.searchVariable(nameExpr.getName());
                //新增全局变量
                if (variable == null) {
                    currentBlock.addGlobalVar(nameExpr.getName(), value);
                } else {
                    variable.setValue(value);
                }
            } else if (expr instanceof DotExpr dotExpr) {
                Table  left = checkTable(dotExpr.getLeft().visitExpr(this));
                checkName(dotExpr.getRight());
                NameExpr nameExpr = (NameExpr) dotExpr.getRight();
                left.add(nameExpr.getName(), value);
            } else if (expr instanceof IndexExpr indexExpr) {
                Table left = checkTable((indexExpr.getLeft()).visitExpr(this));
                Value right = indexExpr.getRight().visitExpr(this);
                left.add(right, value);
            } else {
                throw new RuntimeException("不支持的赋值类型");
            }
        }
    }

    public void executeStatement(WhileStatement whileStatement) {
        circleLevel++;
        Expr cond = whileStatement.getCondition();
        while (cond.visitExpr(this).equals(BoolValue.TRUE)) {
            whileStatement.getBlockStatement().visitStatement(this);
            if (shouldReturn()) {
                return;
            }
            if (shouldBreak()) {
                breakLevel = CLEAR_MARK;
                break;
            }
            if (shouldContinue()) {
                continueLevel = CLEAR_MARK;
            }
        }
        circleLevel--;
    }

    public void executeStatement(BlockStatement blockStatement) {
        Block newBlock = new Block(currentBlock, blockStatement);
        blockLevel++;
        currentBlock = newBlock;
        executeBlock(newBlock);
        ///还原block
        blockLevel--;
        currentBlock = currentBlock.parent;
    }

    public void executeStatement(NumberForStatement statement) {
        //初始值
        NumberValue initValue = (NumberValue) statement.getExpr1().visitExpr(this);
        //终止值
        NumberValue finalValue = (NumberValue) statement.getExpr2().visitExpr(this);
        //补偿
        NumberValue step;
        if (statement.getExpr3() == null) {
            step = new NumberValue(1);
        } else {
            step = (NumberValue) statement.getExpr3().visitExpr(this);
        }
        currentBlock.addVar(statement.getVar(), initValue);
        boolean reverse = finalValue.getF() < initValue.getF();
        circleLevel++;
        for (; reverse ? initValue.getF() >= finalValue.getF() : initValue.getF() <= finalValue.getF(); initValue.setF(initValue.getF() + step.getF())) {
            statement.getBlockStatement().visitStatement(this);
            if (shouldReturn()) {
                break;
            }
            if (shouldBreak()) {
                breakLevel = CLEAR_MARK;
                break;
            }
            if (shouldContinue()) {
                continueLevel = CLEAR_MARK;
            }
        }
        currentBlock.removeVar(statement.getVar());
        circleLevel--;
    }

    /**
     * @param statement
     * @// TODO: 2023/9/16  后期实现
     */
    public void executeStatement(GenericForStatement statement) {

    }

    public void executeStatement(RepeatStatement statement) {
        Expr cond = statement.getCondition();
        circleLevel++;
        do {
            statement.getBlockStatement().visitStatement(this);
            if (shouldReturn()) {
                break;
            }
            if (shouldBreak()) {
                breakLevel = CLEAR_MARK;
                break;
            }
            if (shouldContinue()) {
                continueLevel = CLEAR_MARK;
            }
        } while (cond.visitExpr(this).equals(BoolValue.TRUE));
        circleLevel--;
    }

    public void executeStatement(BreakStatement statement) {
        if (inCircle()) {
            breakLevel = circleLevel;
        } else {
            throw new RuntimeException(statement.toString());
        }
    }


    public void executeStatement(ContinueStatement statement) {
        if (inCircle()) {
            continueLevel = circleLevel;
        } else {
            throw new RuntimeException(statement.toString());
        }
    }

    public void executeStatement(GotoLabelStatement statement) {
        //记录要跳转的标签，具体的跳转在 block里面完成
        this.gotoLabel = statement.getLabelName();
    }

    public void executeStatement(IfStatement statement) {
        if (statement.getIfCond().visitExpr(this).equals(BoolValue.TRUE)) {
            statement.getIfBlock().visitStatement(this);
            return;
        }
        for (int i = 0; i < statement.getElseIfConds().size(); i++) {
            if (statement.getElseIfConds().get(i).visitExpr(this).equals(BoolValue.TRUE)) {
                statement.getElseIfBlocks().get(i).visitStatement(this);
                return;
            }
        }
        if (statement.getElseBlock() != null) {
            statement.getElseBlock().visitStatement(this);
        }
    }

    public void executeStatement(ReturnStatement statement) {
        returned = true;
        if (statement.getExprs().size() == 1) {
            returnValue = statement.getExprs().get(0).visitExpr(this);
        } else {
            List<Value> multi = new ArrayList<>();
            for (Expr expr : statement.getExprs()) {
                multi.add(expr.visitExpr(this));
            }
            returnValue = new MultiValue(multi);
        }
    }

    /**
     * 添加全局函数
     */
    public void executeStatement(FunctionStatement statement) {
        FuncType funcType = statement.getFuncName();
        if (funcType instanceof BasicFuncType) {
            currentBlock.addGlobalVar(((BasicFuncType) funcType).getFuncName(), new com.jdy.lua.data.Function(currentBlock, statement.getFuncBody()));
        } else if (funcType instanceof TableMethod method) {
            Table table = resolveTable(method.getTableNames());
            table.add(method.getMethodName(), new com.jdy.lua.data.Function(currentBlock, statement.getFuncBody()));
        } else if (funcType instanceof TableExtendMethod method) {
            Table table = resolveTable(method.getFatherTableNames());
            table.add(method.getMethodName(), new com.jdy.lua.data.Function(currentBlock, statement.getFuncBody(), true));
        }
    }

    private Table resolveTable(List<String> tableNames) {
        Variable var = currentBlock.searchVariable(tableNames.get(0));
        checkNull(var, "表:" + tableNames.get(0) + "不存在");
        Table result = checkTable(var.getValue());
        for (int i = 1; i < tableNames.size(); i++) {
            result = checkTable(result.get(tableNames.get(i)));
        }
        return result;
    }

    /**
     * 添加一个函数
     */
    public void executeStatement(LocalFunctionStatement statement) {
        currentBlock.addVar(statement.getFuncName(), new com.jdy.lua.data.Function(currentBlock, statement.getFuncBody()));
    }

    public Value executeExpr(Expr expr) {
        return NilValue.NIL;
    }

    /**
     * a.b
     */
    public Value executeExpr(DotExpr expr) {
        Value v = expr.getLeft().visitExpr(this);
        checkTable(v);
        Table table = (Table) v;
        checkName(expr.getRight());
        return table.get(((NameExpr) expr.getRight()).getName());
    }

    public Value executeExpr(IndexExpr expr) {
        Table table = (Table) expr.getLeft().visitExpr(this);
        return table.get(expr.getRight().visitExpr(this));
    }

    public Value executeExpr(AndExpr expr) {
        if (expr.getLeft().visitExpr(this) == TRUE && expr.getRight().visitExpr(this) == TRUE) {
            return TRUE;
        }
        return FALSE;
    }

    public Value executeExpr(OrExpr expr) {
        if (expr.getLeft().visitExpr(this) == TRUE || expr.getRight().visitExpr(this) == TRUE) {
            return TRUE;
        }
        return FALSE;
    }

    public Value executeExpr(CatExpr expr) {
        Value left = expr.getLeft().visitExpr(this);
        Value right = expr.getRight().visitExpr(this);
        if (left instanceof StringValue leftVal && right instanceof StringValue rightVal) {
            return new StringValue(leftVal.getVal() + rightVal.getVal());
        }
        return new StringValue("");
    }

    public Value executeExpr(CalExpr expr) {
        Value left = expr.getLeft().visitExpr(this);
        float l = left instanceof NumberValue ? ((NumberValue) left).getF() : 0;
        Value right = expr.getRight().visitExpr(this);
        float r = right instanceof NumberValue ? ((NumberValue) right).getF() : 0;
        return switch (expr.getOp()) {
            case "+" -> new NumberValue(l + r);
            case "-" -> new NumberValue(l - r);
            case "*" -> new NumberValue(l * r);
            case "/" -> new NumberValue(l / r);
            case "%" -> new NumberValue(l % r);
            case "//" -> new NumberValue(((int) l) % ((int) r));
            case "^" -> new NumberValue((float) Math.pow(l, r));
            case "&" -> new NumberValue(((int) l) & ((int) r));
            case "|" -> new NumberValue(((int) l) | ((int) r));
            case "<<" -> new NumberValue(((int) l) << ((int) r));
            case ">>" -> new NumberValue(((int) l) >> ((int) r));
            case ".." -> new StringValue((StringValue) left, (StringValue) right);
            default -> NIL;
        };
    }

    public Value executeExpr(UnaryExpr expr) {
        Value v = expr.getExpr().visitExpr(this);
        switch (expr.getOp()) {
            case "#" -> {
                if (v instanceof StringValue) {
                    int len = ((StringValue) v).getVal().length();
                    return new NumberValue(len);
                }
            }
            case "not" -> {
                if (v == TRUE) {
                    return FALSE;
                }
                return TRUE;
            }
            case "-" -> {
                if (v instanceof NumberValue) {
                    float f = -1 * ((NumberValue) v).getF();
                    return new NumberValue(f);
                }
            }
            case "~" -> {
                if (v instanceof NumberValue) {
                    int f = ((NumberValue) v).getF().intValue();
                    return new NumberValue(~f);
                }
            }
        }
        return NilValue.NIL;
    }

    public Value executeExpr(RelExpr expr) {
        NumberValue left = (NumberValue) expr.getLeft().visitExpr(this);
        NumberValue right = (NumberValue) expr.getRight().visitExpr(this);
        Float l = left.getF();
        Float r = right.getF();
        return switch (expr.getOp()) {
            case "<" -> l < r ? TRUE : FALSE;
            case ">" -> l > r ? TRUE : FALSE;
            case "<=" -> l <= r ? TRUE : FALSE;
            case ">=" -> l >= r ? TRUE : FALSE;
            case "~=" -> !Objects.equals(l, r) ? TRUE : FALSE;
            case "==" -> Objects.equals(l, r) ? TRUE : FALSE;
            default -> FALSE;
        };
    }

    public Value executeStatement(FuncCallExpr stat) {
        return doFuncCall(stat);
    }

    private Value doFuncCall(FuncCallExpr expr) {
        com.jdy.lua.data.Function function;
        //存储实参
        final List<Value> initArgs = new ArrayList<>();
        //一共两种形式  a.b() a:b()
        Expr funcExpr = expr.getFunc();
        if (funcExpr instanceof ColonExpr colonExpr) {
            //取table，作为self参数
            Table table = checkTable(colonExpr.getLeft().visitExpr(this));
            initArgs.add(table);
            function = checkFunc(table.get(colonExpr.getName()));
        } else{
            function = checkFunc( expr.getFunc().visitExpr(this));
        }
        //求实参的值
        expr.getExprs().forEach(argExpr -> {
            initArgs.add(argExpr.visitExpr(this));
        });
        //调用函数
        Executor executor = new Executor(function,initArgs);
        return executor.execute();
    }

    public Value executeExpr(FuncCallExpr expr) {
        return doFuncCall(expr);
    }

    public Value executeExpr(TableExpr expr) {
        Table table = new Table();
        expr.getExprExprMap().forEach((k, v) -> {
            Value key;
            //key是名称
            if (k instanceof NameExpr nameExpr) {
                key = new StringValue(nameExpr.getName());
            } else {
                //key是表达式
                key = k.visitExpr(this);
            }
            table.add(key, v.visitExpr(this));
        });
        return table;
    }

    public Value executeExpr(ExprList expr) {
        List<Value> valueList = new ArrayList<>();
        for (Expr exp : expr.getExprs()) {
            valueList.add(exp.visitExpr(this));
        }
        return new MultiValue(valueList);
    }


    public Value executeExpr(Expr.Function functionBody) {
        return new Function(currentBlock, functionBody);
    }

    public Value executeExpr(NameExpr expr) {
        return currentBlock.searchVariable(expr.getName()).getValue();
    }

    public Value executeExpr(MultiArg expr) {
        return currentBlock.searchVariable("...").getValue();
    }
}
