package com.jdy.lua.lcodes2;

import com.jdy.lua.lcodes.BinOpr;
import com.jdy.lua.lcodes.UnOpr;
import com.jdy.lua.lobjects.TValue;
import com.jdy.lua.lopcodes.Instruction;
import com.jdy.lua.lopcodes.Instructions;
import com.jdy.lua.lopcodes.OpCode;
import com.jdy.lua.lparser2.ArgAndKind;
import com.jdy.lua.lparser2.FunctionInfo;
import com.jdy.lua.lparser2.TableAccess;
import com.jdy.lua.lparser2.expr.*;
import com.jdy.lua.lparser2.statement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jdy.lua.lparser2.expr.SuffixedExp.SuffixedContent;

public class InstructionGenerator {

    private FunctionInfo fi;

    public InstructionGenerator(FunctionInfo fi) {
        this.fi = fi;
    }

    /**
     * 用于 存储 fi中的寄存器数量
     */
    private int oldRegs;

    /**
     * 存储寄存器
     */
    public void storeRegs() {
        oldRegs = fi.getUsedRegs();
    }

    /**
     * 还原寄存器
     */
    public void loadRegs() {
        fi.setUsedRegs(oldRegs);
    }


    public void generate(Expr expr, int a, int n) {
    }

    public void generate(Statement statement) {

    }

    public void generate(LocalFuncStat funcStat) {
        int r = fi.addLocVar(funcStat.getStr(), fi.getPc() + 2);
        funcStat.getFunctionBody().generate(this, r, 0);
    }

    /**
     *
     */
    public void generate(FunctionStat functionStat) {
        storeRegs();
        if(functionStat.getFieldDesc() != null && functionStat.getFieldDesc().size() > 0){
            List<NameExpr> nameExprs = functionStat.getFieldDesc();
            int a = fi.allocReg();
            //将结果都存放在a
            tableAccess(nameExprs.get(0),nameExprs.get(1),a);
            for(int i=1;i<nameExprs.size();i++){
                int c = exp2ArgAndKind(fi,)
            }

        } else{
           NameExpr a;
        }


        loadRegs();

    }


    public void generate(StatList statList) {
        for (Statement statement : statList.getStatements()) {
            statement.generate(this);
        }
    }

    public void generate(BlockStatement blockStatement, FunctionInfo fi) {
        blockStatement.getStatList().generate(this);
    }


    public void generate(RepeatStatement repeatStatement) {
        fi.enterScope(true);
        int pcBeforeBlock = fi.getPc();
        repeatStatement.getBlock().generate(this);
        storeRegs();
        int a = exp2ArgAndKind(fi, repeatStatement.getCond(), ArgAndKind.ARG_REG).getArg();
        loadRegs();
        Lcodes.emitCodeABC(fi, OpCode.OP_TEST, a, 0, 0);
        Lcodes.emitCodeJump(fi, pcBeforeBlock - fi.getPc() - 1, 0);
        fi.closeOpnUpval();
        fi.exitScope(fi.getPc() + 1);
    }

    public void generate(ReturnStatement returnStatement) {
        if (returnStatement.getExprList() == null) {
            Lcodes.emitCodeABC(fi, OpCode.OP_RETURN, 0, 1, 0);
            return;
        }
        List<Expr> exprs = returnStatement.getExprList().getExprList();
        int nExprs = exprs.size();
        if (nExprs == 1) {
            if (exprs.get(0) instanceof NameExpr) {
                NameExpr nameExp = (NameExpr) exprs.get(0);
                int r = fi.slotOfLocVar(nameExp.getName());
                if (r >= 0) {
                    Lcodes.emitCodeABC(fi, OpCode.OP_RETURN, r, 2, 0);
                    return;
                }
            }
            if (exprs.get(0) instanceof SuffixedExp) {
                //有可能是函数调用

                return;
            }

        }

        boolean multRet = hasMultiRet(exprs.get(exprs.size() - 1));
        for (int i = 0; i < nExprs; i++) {
            Expr expr = exprs.get(i);
            int r = fi.allocReg();
            if (i == nExprs - 1 && multRet) {
                expr.generate(this, r, -1);
            } else {
                expr.generate(this, r, 1);
            }
        }
        fi.freeReg(nExprs);

        int a = fi.getUsedRegs();
        if (multRet) {
            Lcodes.emitCodeABC(fi, OpCode.OP_RETURN, a, 0, 0);
        } else {
            Lcodes.emitCodeABC(fi, OpCode.OP_RETURN, a, nExprs + 1, 0);
        }
    }

    public void generate(BreakStatement breakStatement) {
        int pc = Lcodes.emitCodeJump(fi, 0, 0);
        fi.addBreakJmp(pc);
    }

    public void generate(WhileStatement whileStatement) {
        int pcBeforeExp = fi.getPc();
        storeRegs();
        int a = exp2ArgAndKind(fi, whileStatement.getCond(), ArgAndKind.ARG_REG).getArg();
        loadRegs();
        Lcodes.emitCodeABC(fi, OpCode.OP_TEST, a, 0, 0);
        int pcJmpToEnd = Lcodes.emitCodeJump(fi, 0, 0);
        fi.enterScope(true);
        whileStatement.getBlock().generate(this, 0, 0);
        fi.closeOpnUpval();
        Lcodes.emitCodeJump(fi, pcBeforeExp - fi.getPc() - 1, 0);
        fi.exitScope(fi.getPc());
        Instruction ins = fi.getInstruction(pcJmpToEnd);
        Instructions.setArgsJ(ins, fi.getPc() - pcJmpToEnd);
    }

    public void generate(IfStatement ifStatement) {
        int expNum = 1 + ifStatement.getElseThenConds().size();
        int[] pcJumpsToEnds = new int[expNum];
        int pcJmpToNextExp = -1;

        for (int i = 0; i < expNum; i++) {
            Expr expr;
            if (i == 0) {
                expr = ifStatement.getCond();
            } else {
                expr = ifStatement.getElseThenConds().get(i - 1);
            }
            //调整jump的调整位置，pcJumpToNextExp是上个表达式里jmp指令的pc
            if (pcJmpToNextExp >= 0) {
                Instruction jmpIns = fi.getInstruction(pcJmpToNextExp);
                Instructions.setArgsJ(jmpIns, fi.getPc() - pcJmpToNextExp);
            }
            storeRegs();
            int a = exp2ArgAndKind(fi, expr, ArgAndKind.ARG_REG).getArg();
            loadRegs();

            Lcodes.emitCodeABC(fi, OpCode.OP_TEST, a, 0, 0);
            pcJmpToNextExp = Lcodes.emitCodeJump(fi, 0, 0);
            fi.enterScope(false);
            if (i == 0) {
                ifStatement.getBlockStatement().generate(this, 0, 0);
            } else {
                ifStatement.getElseThenBlock().get(i - 1).generate(this, 0, 0);
            }
            fi.closeOpnUpval();
            fi.exitScope(fi.getPc() + 1);
            if (i < expNum - 1 || ifStatement.getElseBlock() != null) {
                pcJumpsToEnds[i] = Lcodes.emitCodeJump(fi, 0, 0);
            } else {
                pcJumpsToEnds[i] = pcJmpToNextExp;
            }

        }
        //如果有else
        if (ifStatement.getElseBlock() != null) {
            fi.enterScope(false);
            ifStatement.getElseBlock().generate(this, 0, 0);
            fi.closeOpnUpval();
            fi.exitScope(fi.getPc() + 1);
        }
        for (int pc : pcJumpsToEnds) {
            Instruction in = fi.getInstruction(pc);
            Instructions.setArgsJ(in, fi.getPc() - pc);
        }
    }

    public void removeTailNils(ExprList exprList) {
        List<Expr> exprs = exprList.getExprList();
        while (!exprs.isEmpty() && exprs.get(exprs.size() - 1) instanceof NilExpr) {
            exprs.remove(exprs.size() - 1);
        }
    }

    private SuffixedExp simplify(SuffixedExp suffixedExp) {
        while (suffixedExp.getSuffixedContent() == null && suffixedExp.getPrimaryExr() instanceof SuffixedExp) {
            suffixedExp = (SuffixedExp) suffixedExp.getPrimaryExr();
        }
        return suffixedExp;
    }

    public void generate(ExprStatement exprStatement) {
        //函数调用
        if (exprStatement.getFunc() != null) {
            int r = fi.allocReg();
            exprStatement.getFunc().generate(this, r, 0);
            fi.freeReg();
            return;
        }
        List<SuffixedExp> vars = exprStatement.getLefts();
        List<Expr> exprs = exprStatement.getRight().getExprList();
        //赋值 a,b,c=1,2,3
        removeTailNils(exprStatement.getRight());
        int nVars = exprStatement.getLefts().size();
        int nExps = exprs.size();
        int[] tableRegs = new int[nVars];
        int[] keyRegs = new int[nVars];
        //将值存放到varRegs
        int[] varRegs = new int[nVars];
        storeRegs();

        for (int i = 0; i < vars.size(); i++) {
            SuffixedExp suffixedExp = vars.get(i);
            suffixedExp = simplify(suffixedExp);
            TableAccess tableAccess = suffixedExp.tryTrans2TableAccess();
            if (tableAccess != null) {
                //存放 table
                tableRegs[i] = fi.allocReg();
                tableAccess.getTable().generate(this, tableRegs[i], 1);
                keyRegs[i] = fi.allocReg();
                tableAccess.getKey().generate(this, keyRegs[i], 1);
            } else {
                NameExpr nameExpr = (NameExpr) suffixedExp.getPrimaryExr();
                String name = nameExpr.getName();
                if (fi.slotOfLocVar(name) < 0 && fi.indexOfUpval(name) < 0) {
                    keyRegs[i] = -1;
                    //全局变量
                    if (fi.indexOfConstant(TValue.strValue(name)) > 0xFF) {
                        keyRegs[i] = fi.allocReg();
                    }
                }
            }
        }
        //实现存储好变量值的寄存器，后面才会 alloc
        for (int i = 0; i < vars.size(); i++) {
            varRegs[i] = fi.getUsedRegs() + i;
        }
        if (nExps >= nVars) {
            for (int i = 0; i < exprs.size(); i++) {
                Expr exp = exprs.get(i);
                int a = fi.allocReg();
                if (i >= nVars && i == nExps - 1 && hasMultiRet(exp)) {
                    exp.generate(this, a, 0);
                } else {
                    exp.generate(this, a, 1);
                }
            }
        } else {
            boolean multRet = false;
            for (int i = 0; i < exprs.size(); i++) {
                Expr exp = exprs.get(i);
                int a = fi.allocReg();
                if (i == nExps - 1 && hasMultiRet(exp)) {
                    multRet = true;
                    int n = nVars - nExps + 1;
                    exp.generate(this, a, n);
                    fi.allocReg(n - 1);
                } else {
                    exp.generate(this, a, 1);
                }
            }
            if (!multRet) {
                int n = nVars - nExps;
                int a = fi.allocReg(n);
                Lcodes.emitCodeABC(fi, OpCode.OP_LOADNIL, a, n - 1, 0);
            }
        }
        for (int i = 0; i < nVars; i++) {
            SuffixedExp suffixedExp = simplify(vars.get(i));
            if (suffixedExp.tryTrans2TableAccess() != null) {
                Lcodes.emitCodeABC(fi, OpCode.OP_SETTABLE, tableRegs[i], keyRegs[i], varRegs[i]);
                continue;
            }
            NameExpr nameExpr = (NameExpr) suffixedExp.getPrimaryExr();
            String varName = nameExpr.getName();
            int a = fi.slotOfLocVar(varName);
            if (a >= 0) {
                Lcodes.emitCodeABC(fi, OpCode.OP_MOVE, a, varRegs[i], 0);
                continue;
            }

            int b = fi.indexOfUpval(varName);
            if (b >= 0) {
                Lcodes.emitCodeABC(fi, OpCode.OP_SETUPVAL, varRegs[i], b, 0);
                continue;
            }
            int env = fi.slotOfLocVar("_ENV");
            if (env >= 0) {
                if (keyRegs[i] < 0) {
                    b = 0x100 + fi.indexOfConstant(TValue.strValue(varName));
                    Lcodes.emitCodeABC(fi, OpCode.OP_SETFIELD, env, b, varRegs[i]);
                } else {
                    Lcodes.emitCodeABC(fi, OpCode.OP_SETTABLE, env, keyRegs[i], varRegs[i]);
                }
                continue;
            }
            //全局变量
            env = fi.indexOfUpval("_ENV");
            if (keyRegs[i] < 0) {
                b = 0x100 + fi.indexOfConstant(TValue.strValue(varName));
                Lcodes.emitCodeABC(fi, OpCode.OP_SETFIELD, env, b, varRegs[i]);
            } else {
                Lcodes.emitCodeABC(fi, OpCode.OP_SETTABLE, env, keyRegs[i], varRegs[i]);
            }

        }
        loadRegs();
    }

    public void generate(FunctionBody functionBody, int a, int n) {
        FunctionInfo subFunc = new FunctionInfo();
        InstructionGenerator instructionGenerator = new InstructionGenerator(subFunc);
        fi.addFunc(subFunc);
        for (NameExpr nameExpr : functionBody.getParList().getNameExprs()) {
            subFunc.addLocVar(nameExpr.getName(), 0);
        }
        if(functionBody.isMethod()){
            fi.addLocVar("self",0);
        }
        functionBody.getBlock().generate(instructionGenerator, subFunc);
        subFunc.exitScope(subFunc.getPc() + 2);
        Lcodes.emitCodeABC(subFunc, OpCode.OP_RETURN, 0, 1, 0);
        int bx = fi.getSubFuncs().size() - 1;
        Lcodes.emitCodeABx(fi, OpCode.OP_CLOSURE, a, bx);
    }

    public void generate(LocalStatement statement) {
        removeTailNils(statement.getExprList());
        storeRegs();
        List<Expr> exprList = statement.getExprList().getExprList();
        List<NameExpr> nameExprs = statement.getNameExprList();
        int nExps = exprList.size();
        int nNames = nameExprs.size();

        //表达式数量和变量数量一致
        if (nExps == nNames) {
            for (Expr expr : exprList) {
                int tempReg = fi.allocReg();
                expr.generate(this, tempReg, 1);
            }
        } else if (nExps > nNames) {
            for (int i = 0; i < nExps; i++) {
                Expr expr = exprList.get(i);
                int tempReg = fi.allocReg();
                if (i == nExps - 1 && hasMultiRet(expr)) {
                    expr.generate(this, tempReg, 0);
                } else {
                    expr.generate(this, tempReg, 1);
                }
            }

        } else {
            boolean hasMulRet = false;
            for (int i = 0; i < nExps; i++) {
                Expr expr = exprList.get(i);
                int tempReg = fi.allocReg();
                if (i == nExps - 1 && hasMultiRet(expr)) {
                    hasMulRet = true;
                    int tempReg2 = nNames - nExps + 1;
                    expr.generate(this, tempReg, tempReg2);
                    //为多个返回值，分配空间
                    fi.allocReg(tempReg2 - 1);
                } else {
                    expr.generate(this, tempReg, 1);
                }
            }
            //置nil
            if (!hasMulRet) {
                int nilNum = nNames - nExps;
                int tempReg = fi.allocReg(nilNum);
                Lcodes.emitCodeABC(fi, OpCode.OP_LOADNIL, tempReg, nilNum - 1, 0);
            }
        }
        loadRegs();
        int startPc = fi.getPc() + 1;
        for (NameExpr expr : nameExprs) {
            fi.addLocVar(expr.getName(), startPc);
        }
    }

    public void generate(TableConstructor constructor, int a, int n) {
        //数组部分大小
        int nArr = constructor.getListFields().size();
        //map部分大小
        int nExp = constructor.getFields().size();

        boolean hasMulRet = false;
        if (nArr != 0) {
            hasMulRet = hasMulRet || hasMultiRet(constructor.getListFields().get(0));
        }
        if (nExp != 0) {
            hasMulRet = hasMulRet || hasMultiRet(constructor.getFields().get(0));
        }
        Lcodes.emitCodeABC(fi, OpCode.OP_NEWTABLE, a, nArr, nExp);
        //处理数组部分
        for (int i = 1; i <= nArr; i++) {
            Expr listField = constructor.getListFields().get(i - 1).getExpr();
            int tmp = fi.allocReg();
            if (i == nArr && hasMulRet) {
                listField.generate(this, tmp, -1);
            } else {
                listField.generate(this, tmp, 1);
            }
            if (i % 50 == 0 || i == nArr) {
                int reg = i % 50;
                if (reg == 0) {
                    reg = 50;
                }
                fi.freeReg(reg);
                int c = (i - 1) / 50 + 1;
                if (i == nArr && hasMulRet) {
                    Lcodes.emitCodeABC(fi, OpCode.OP_SETLIST, a, 0, c);
                } else {
                    Lcodes.emitCodeABC(fi, OpCode.OP_SETLIST, a, reg, c);
                }
            }
        }
        //处理 table部分
        for (int i = 1; i <= nExp; i++) {
            TableField field = constructor.getFields().get(i - 1);
            Expr left = field.getLeft();
            Expr right = field.getRight();
            int b = fi.allocReg();
            left.generate(this, b, 1);
            int c = fi.allocReg();
            right.generate(this, c, 1);
            fi.freeReg(2);
            Lcodes.emitCodeABC(fi, OpCode.OP_SETTABLE, a, b, c);
        }

    }

    private LocalStatement forNum2LocalStatement(ForStatement forStatement) {
        LocalStatement localStatement = new LocalStatement();
        NameExpr name1 = new NameExpr("(for index)");
        NameExpr name2 = new NameExpr("(for limit)");
        NameExpr name3 = new NameExpr("(for step)");
        localStatement.getNameExprList().addAll(Arrays.asList(name1, name2, name3));
        localStatement.getExprList().addExpr(forStatement.getExpr1());
        localStatement.getExprList().addExpr(forStatement.getExpr2());
        localStatement.getExprList().addExpr(forStatement.getExpr3());
        return localStatement;
    }

    private void forNum(ForStatement forStatement) {
        if (forStatement.getExpr3() == null) {
            //默认步长
            Expr expr = new IntExpr(1L);
            forStatement.setExpr3(expr);
        }

        fi.enterScope(true);
        //定义循环用的变量
        LocalStatement localStatement = forNum2LocalStatement(forStatement);
        generate(localStatement);
        fi.addLocVar(forStatement.getName1().getName(), fi.getPc() + 2);

        int a = fi.getUsedRegs() - 4;
        int pcForPrep = Lcodes.emitCodeABx(fi, OpCode.OP_FORPREP, a, 0);
        forStatement.getBlock().generate(this);
        fi.closeOpnUpval();

        int pcForLoop = Lcodes.emitCodeABx(fi, OpCode.OP_FORLOOP, a, 0);

        Instruction prep = fi.getInstruction(pcForPrep);
        Instruction loop = fi.getInstruction(pcForLoop);
        Instructions.setArgsBx(prep, pcForLoop - pcForPrep - 1);
        Instructions.setArgsBx(loop, pcForPrep - pcForLoop);

        fi.exitScope(fi.getPc());

        fi.fixEndPC("(for index)", 1);
        fi.fixEndPC("(for limit)", 1);
        fi.fixEndPC("(for step)", 1);

    }

    private LocalStatement forIn2Localstatement(ForStatement forStatement) {
        LocalStatement localStatement = new LocalStatement();
        NameExpr name1 = new NameExpr("(for generator)");
        NameExpr name2 = new NameExpr("(for state)");
        NameExpr name3 = new NameExpr("(for control)");
        localStatement.getNameExprList().addAll(Arrays.asList(name1, name2, name3));
        localStatement.setExprList(forStatement.getExprList());
        return localStatement;
    }

    private void forIn(ForStatement forStatement) {
        fi.enterScope(true);
        LocalStatement localStatement = forIn2Localstatement(forStatement);
        localStatement.generate(this);
        for (NameExpr expr : forStatement.getNameExprList()) {
            fi.addLocVar(expr.getName(), fi.getPc() + 2);
        }
        int pcJmpToTFC = Lcodes.emitCodeJump(fi, 0, 0);
        forStatement.getBlock().generate(this);
        fi.closeOpnUpval();
        Instruction tfcIns = fi.getInstruction(pcJmpToTFC);
        Instructions.setArgsJ(tfcIns, fi.getPc() - pcJmpToTFC);

        int rGenerator = fi.slotOfLocVar("(for generator)");
        Lcodes.emitCodeABC(fi, OpCode.OP_TFORCALL, rGenerator, forStatement.getNameExprList().size(), 0);
        Lcodes.emitCodeABx(fi, OpCode.OP_TFORLOOP, rGenerator + 2, pcJmpToTFC - fi.getPc() - 1);
        fi.exitScope(fi.getPc() - 1);
        fi.fixEndPC("(for generator)", 2);
        fi.fixEndPC("(for state)", 2);
        fi.fixEndPC("(for control)", 2);

    }

    public void generate(ForStatement forStatement) {
        if (forStatement.isGeneric()) {
            forIn(forStatement);
        } else {
            forNum(forStatement);
        }
    }

    public void generate(SuffixedExp suffixedExp, int a, int n) {
        if (suffixedExp.getSuffixedContent() == null) {
            suffixedExp.getPrimaryExr().generate(this, a, n);
            return;
        }
        Expr primary = suffixedExp.getPrimaryExr();
        SuffixedContent content = suffixedExp.getSuffixedContent();
        //a.b
        if (content.isHasDot()) {
            tableAccess(primary, content.getNameExpr(), a);
            //a[b]
        } else if (content.getTableIndex() != null) {
            tableAccess(primary, content.getTableIndex().getExpr(), a);
            //a:b()
        } else if (content.isHasColon()) {
            methodCall(primary, content.getNameExpr(), content.getFuncArgs(), a, n);
            //a()
        } else if(content.getFuncArgs() != null) {
            funcCall(primary, content.getFuncArgs(), a, n);
        }
    }

    public static boolean hasMultiRet(Expr expr) {
        if (expr instanceof VarargExpr) {
            return true;
        }
        //func Call
        if (expr instanceof SuffixedExp) {
            SuffixedExp suf = (SuffixedExp) expr;
            if (suf.getSuffixedContent() != null && suf.getSuffixedContent().getFuncArgs() != null) {
                return true;
            }
        }

        return false;
    }

    private int prepareFuncCall(Expr expr, NameExpr name, FuncArgs args, int a, int n) {
        List<Expr> exprList = new ArrayList<>();
        //函数参数有三种类型 a "hello"  a(x,x,x)  a{a=1,b=2,c=3}
        if (args.getExpr1().size() != 0) {
            exprList = args.getExpr1();
        } else if (args.getConstructor() != null) {
            exprList.add(args.getConstructor());
        } else if (args.getStringExpr() != null) {
            exprList.add(args.getStringExpr());
        }
        int nArgs = exprList.size();
        boolean hasMultiRet = false;

        expr.generate(this, a, 1);
        //method Call
        if (name != null) {
            fi.allocReg();
            ArgAndKind argAndKindC = exp2ArgAndKind(fi, name, ArgAndKind.ARG_REG);
            Lcodes.emitCodeABC(fi, OpCode.OP_SELF, a, a, argAndKindC.getArg());
            fi.freeReg(1);
        }
        for (int i = 0; i < exprList.size(); i++) {
            Expr ex = exprList.get(i);
            int tempReg = fi.allocReg();
            if (i == exprList.size() - 1 && hasMultiRet(ex)) {
                hasMultiRet = true;
                ex.generate(this, tempReg, -1);
            } else {
                ex.generate(this, tempReg, 1);
            }
        }
        fi.freeReg(nArgs);

        if (name != null) {
            nArgs++;
        }

        if (hasMultiRet) {
            nArgs = -1;
        }
        return nArgs;
    }

    private void methodCall(Expr expr, NameExpr name, FuncArgs args, int a, int n) {
        int nArgs = prepareFuncCall(expr, name, args, a, n);
        //b c 分别为参数数量 和 函数的返回值数量
        Lcodes.emitCodeABC(fi, OpCode.OP_CALL, a, nArgs + 1, n + 1);

    }

    private void funcCall(Expr exp, FuncArgs args, int a, int n) {
        int nArgs = prepareFuncCall(exp, null, args, a, n);
        Lcodes.emitCodeABC(fi, OpCode.OP_CALL, a, nArgs + 1, n + 1);
    }

    private void tableAccess(TableAccess access, int a) {
        tableAccess(access.getTable(), access.getKey(), a);
    }

    /**
     * exrp[key] =valu
     */
    private void tableSet(Expr t,Expr k,Expr v){
        storeRegs();
        ArgAndKind argAndKind = exp2ArgAndKind(fi,t,ArgAndKind.ARG_RU);
        int a = argAndKind.getArg();
        int b = exp2ArgAndKind(fi,k,ArgAndKind.ARG_RK).getArg();
        int c = exp2ArgAndKind(fi,v,ArgAndKind.ARG_REG).getArg();
        if(argAndKind.getKind() == ArgAndKind.ARG_REG){
            Lcodes.emitCodeABC(fi,OpCode.OP_SETTABLE,a,b,c);
        } else{
            Lcodes.emitCodeABC(fi,OpCode.OP_SETTABUP,a,b,c);
        }
        loadRegs();
    }
    /**
     * exp[key]
     */
    private void tableAccess(Expr exp, Expr key, int a) {
        storeRegs();
        ArgAndKind argAndKindB = exp2ArgAndKind(fi, exp, ArgAndKind.ARG_RU);
        int b = argAndKindB.getArg();
        int c = exp2ArgAndKind(fi, key, ArgAndKind.ARG_RK).getArg();
        storeRegs();
        if (argAndKindB.getKind() == ArgAndKind.ARG_REG) {
            Lcodes.emitCodeABC(fi, OpCode.OP_GETTABLE, a, b, c);
        } else {
            Lcodes.emitCodeABC(fi, OpCode.OP_GETTABUP, a, b, c);
        }
        loadRegs();
    }

    public void generate(NameExpr expr, int a, int n) {
        int r = fi.slotOfLocVar(expr.getName());
        if (r >= 0) {
            Lcodes.emitCodeABC(fi, OpCode.OP_MOVE, a, r, 0);
            return;
        }
        r = fi.indexOfUpval(expr.getName());
        if (r >= 0) {
            Lcodes.emitCodeABC(fi, OpCode.OP_GETUPVAL, a, r, 0);
            return;
        }
        //env['name'],env存放全局的东西
        NameExpr expr1 = new NameExpr("_ENV");
        tableAccess(expr1, expr, a);
    }

    public void generate(SubExpr subExpr, int a, int n) {
        int b = -1;
        //有单个操作符，需要优先进行处理
        if (subExpr.getUnOpr() != null && subExpr.getUnOpr() != UnOpr.OPR_NOUNOPR) {
            storeRegs();
            //将表达式存进寄存器b里面去
            b = exp2ArgAndKind(fi, subExpr.getSubExpr1(), ArgAndKind.ARG_REG).getArg();
            switch (subExpr.getUnOpr()) {
                case OPR_NOT:
                    Lcodes.emitCodeABC(fi, OpCode.OP_NOT, a, b, 0);
                    break;
                case OPR_BNOT:
                    Lcodes.emitCodeABC(fi, OpCode.OP_BNOT, a, b, 0);
                    break;
                case OPR_LEN:
                    Lcodes.emitCodeABC(fi, OpCode.OP_LEN, a, b, 0);
                    break;
                case OPR_MINUS:
                    Lcodes.emitCodeABC(fi, OpCode.OP_UNM, a, b, 0);
                    break;
                default:
                    break;
            }
            loadRegs();
        }
        // subExpr只有，一个表达式，无任何运算符号
        if (b == -1 && subExpr.getBinOpr() == null && subExpr.getUnOpr() == null && subExpr.getSubExpr2() == null) {
            subExpr.getSubExpr1().generate(this, a, n);
            return;
        }

        //接着处理第二个操作数, and 和 or单独处理
        if (subExpr.getBinOpr() == BinOpr.OPR_AND || subExpr.getBinOpr() == BinOpr.OPR_OR) {
            //表示左边的第一个表达式还未处理
            if (b == -1) {
                storeRegs();
                b = exp2ArgAndKind(fi, subExpr.getSubExpr1(), ArgAndKind.ARG_REG).getArg();
                loadRegs();
            }
            //and 和 or 的跳转方向相反
            if (subExpr.getBinOpr() == BinOpr.OPR_AND) {
                Lcodes.emitCodeABC(fi, OpCode.OP_TESTSET, a, b, 0);
            } else {
                Lcodes.emitCodeABC(fi, OpCode.OP_TESTSET, a, b, 1);
            }
            int jmpPc = Lcodes.emitCodeJump(fi, 0, 0);

            b = exp2ArgAndKind(fi, subExpr.getSubExpr2(), ArgAndKind.ARG_REG).getArg();
            loadRegs();
            Lcodes.emitCodeABC(fi, OpCode.OP_MOVE, a, b, 0);
            //获取当前指令的位置
            int curPc = fi.getPc();
            Instructions.setArgsJ(fi.getInstruction(jmpPc), curPc);
            //统一处理其他操作符
        } else {
            if (b == -1) {
                storeRegs();
                b = exp2ArgAndKind(fi, subExpr.getSubExpr1(), ArgAndKind.ARG_REG).getArg();
            }
            int c = exp2ArgAndKind(fi, subExpr.getSubExpr2(), ArgAndKind.ARG_REG).getArg();
            Lcodes.emitBinaryOp(fi, subExpr.getBinOpr(), a, b, c);
            loadRegs();
        }
    }

    public void generate(SimpleExpr expr, int a, int n) {
        expr.getExpr().generate(this, a, n);
    }

    public void generate(VarargExpr expr, int a, int n) {
        Lcodes.emitCodeABC(fi, OpCode.OP_VARARG, a, n + 1, 0);
    }

    public void generate(NilExpr expr, int a, int n) {
        Lcodes.emitCodeABC(fi, OpCode.OP_LOADNIL, a, n - 1, 0);
    }

    public void generate(TrueExpr expr, int a, int n) {
        Lcodes.emitCodeABC(fi, OpCode.OP_LOADTRUE, a, 0, 0);
    }

    public void generate(FalseExpr expr, int a, int n) {
        Lcodes.emitCodeABC(fi, OpCode.OP_LOADFALSE, a, 0, 0);
    }

    public void generate(FloatExpr expr, int a, int n) {
        int k = fi.indexOfConstant(TValue.doubleValue(expr.getF()));
        Lcodes.emitCodeK(fi, a, k);
    }

    public void generate(IntExpr expr, int a, int n) {
        int k = fi.indexOfConstant(TValue.intValue(expr.getI()));
        Lcodes.emitCodeK(fi, a, k);
    }

    public void generate(StringExpr expr, int a, int n) {
        int k = fi.indexOfConstant(TValue.strValue(expr.getStr()));
        Lcodes.emitCodeK(fi, a, k);
    }

    /**
     * 将 表达式进行处理，结果存储在 返回的 ArgAndKind对象里面 kind表示，存储的类型，
     */
    public ArgAndKind exp2ArgAndKind(FunctionInfo fi, Expr expr, int kind) {
        if (expr instanceof NameExpr) {
            //从函数的 localVar中查找变量
            if ((kind & ArgAndKind.ARG_REG) != 0) {
                int r = fi.slotOfLocVar(((NameExpr) expr).getName());
                if (r != -1) {
                    return new ArgAndKind(r, ArgAndKind.ARG_REG);
                }
            }
            //从 UpVal中查找变量
            if ((kind & ArgAndKind.ARG_UPVAL) != 0) {
                int r = fi.indexOfUpval(((NameExpr) expr).getName());
                if (r != -1) {
                    return new ArgAndKind(r, ArgAndKind.ARG_UPVAL);
                }
            }
        }
        int a = fi.allocReg();
        expr.generate(this, a, 1);
        return new ArgAndKind(a, ArgAndKind.ARG_REG);
    }


}
