package com.jdy.lua.parser;

import com.jdy.lua.antlr4.LuaParser;
import com.jdy.lua.data.BoolValue;
import com.jdy.lua.data.NilValue;
import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.StringValue;
import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;
import com.jdy.lua.util.StringUtil;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jdy
 * @title: Parser
 * @description:
 * @data 2023/9/15 15:44
 */
public class Parser {
    /**
     * 代码块
     */
    public static Statement parseBlock(LuaParser.BlockContext block) {
        Statement.BlockStatement blockStatement = new Statement.BlockStatement();
        for (LuaParser.StatContext stat : block.stat()) {
            blockStatement.addStatement(parseStat(stat));
        }
        if (block.laststat() != null) {
            LuaParser.LaststatContext lastStat = block.laststat();
            String text = lastStat.getStart().getText();
            if ("break".equals(text)) {
                blockStatement.addStatement(new Statement.BreakStatement());
            } else if ("continue".equals(text)) {
                blockStatement.addStatement(new Statement.ContinueStatement());
            } else {
                blockStatement.addStatement(new Statement.ReturnStatement(parseExprList(lastStat.explist())));
            }
        }
        return blockStatement;
    }

    /**
     * 语句
     */
    public static Statement parseStat(LuaParser.StatContext stat) {
        Token token = stat.getStart();
        if (stat.label() != null) {
            return parseLabelStat(stat.label());
        }
        if (stat.varlist() != null) {
            return parseAssignStatement(stat);
        }
        if (stat.functioncall() != null) {
            return parseFuncCall(stat);
        }

        String text = token.getText();
        if (":".equals(text)) {
            return new Statement.EmptyStatement();
        }
        if ("goto".equals(text)) {
            return new Statement.GotoLabelStatement(stat.NAME().getText());
        }
        if ("break".equals(text)) {
            return new Statement.BreakStatement();
        }
        if ("do".equals(text)) {
            return parseBlock(stat.block(0));
        }
        if ("while".equals(text)) {
            return parseWhile(stat);
        }
        if ("repeat".equals(text)) {
            return parseRepeat(stat);
        }
        if ("if".equals(text)) {
            return parseIf(stat);
        }
        if ("for".equals(text)) {
            return parseFor(stat);
        }
        if ("function".equals(text)) {
            return parseFunc(stat);
        }
        if ("local".equals(text)) {
            if (stat.NAME() != null) {
                return new Statement.LocalFunctionStatement(stat.NAME().getText(), parseFuncBody(stat.funcbody()));
            }
            return parseAttnameList(stat);
        }
        throw new RuntimeException("不支持的语法类型");
    }

    /**
     * 标签
     */
    public static Statement parseLabelStat(LuaParser.LabelContext label) {
        return new Statement.LabelStatement(label.NAME().getText());
    }

    /**
     * while语句
     */
    public static Statement parseWhile(LuaParser.StatContext stat) {
        return new Statement.WhileStatement(parseExpr(stat.exp(0)), (Statement.BlockStatement) parseBlock(stat.block(0)));
    }

    public static Statement parseRepeat(LuaParser.StatContext stat) {
        return new Statement.RepeatStatement(parseExpr(stat.exp(0)), (Statement.BlockStatement) parseBlock(stat.block(0)));
    }

    /**
     * if语句
     */
    public static Statement parseIf(LuaParser.StatContext ifStat) {
        Expr ifExpr = parseExpr(ifStat.exp(0));
        Statement.BlockStatement ifBlock = (Statement.BlockStatement) parseBlock(ifStat.block(0));
        List<Expr> elseExprs = new ArrayList<>();
        for (int i = 1; i < ifStat.exp().size(); i++) {
            elseExprs.add(parseExpr(ifStat.exp(i)));
        }
        List<Statement.BlockStatement> elseBlock = new ArrayList<>();
        for (int i = 1; i < ifStat.block().size() - 1; i++) {
            elseBlock.add((Statement.BlockStatement) parseBlock(ifStat.block(i)));
        }
        Statement.BlockStatement end = null;
        if (ifStat.block().size() > 1) {
            end = (Statement.BlockStatement) parseBlock(ifStat.block(ifStat.block().size() - 1));
        }
        return new Statement.IfStatement(ifExpr, ifBlock, elseExprs, elseBlock, end);
    }

    public static Statement parseFor(LuaParser.StatContext forStat) {
        // 数值for循环
        if (forStat.NAME() != null) {
            Statement.NumberForStatement numberForStatement = new Statement.NumberForStatement();
            numberForStatement.setVar(forStat.NAME().getText());
            List<LuaParser.ExpContext> exps = forStat.exp();
            numberForStatement.setExpr1(parseExpr(exps.get(0)));
            numberForStatement.setExpr2(exps.size() >= 2 ? parseExpr(exps.get(1)) : null);
            numberForStatement.setExpr3(exps.size() == 3 ? parseExpr(exps.get(2)) : null);
            numberForStatement.setBlockStatement((Statement.BlockStatement) parseBlock(forStat.block(0)));
            return numberForStatement;
        }
        // 泛型for循环
        Statement.GenericForStatement genericForStatement = new Statement.GenericForStatement();
        genericForStatement.setVars(parseNameList(forStat.namelist()));
        genericForStatement.setExpList(parseExprList(forStat.explist()));
        genericForStatement.setBlockStatement((Statement.BlockStatement) parseBlock(forStat.block(0)));
        return genericForStatement;
    }

    public static List<Expr> parseExprList(LuaParser.ExplistContext exps) {
        List<Expr> exprs = new ArrayList<>();
        exps.exp().forEach(e ->
                exprs.add(parseExpr(e))
        );
        return exprs;
    }

    public static Expr.ExprList parseExprList2(LuaParser.ExplistContext exps) {
        List<Expr> exprs = new ArrayList<>();
        exps.exp().forEach(e ->
                exprs.add(parseExpr(e))
        );
        return new Expr.ExprList(exprs);
    }

    public static List<String> parseNameList(LuaParser.NamelistContext namelistContexts) {
        List<String> vars = new ArrayList<>();
        for (TerminalNode name : namelistContexts.NAME()) {
            vars.add(name.getText());
        }
        return vars;
    }

    public static Statement parseFunc(LuaParser.StatContext funcStat) {
        Statement.FuncType funcType = parseFuncType(funcStat.funcname());
        Expr.Function body = parseFuncBody(funcStat.funcbody());
        return new Statement.FunctionStatement(funcType, body);
    }

    public static Statement.FuncType parseFuncType(LuaParser.FuncnameContext funcName) {
        List<ParseTree> nodes = funcName.children;
        //只有函数名称
        if (nodes.size() == 1) {
            return new Statement.BasicFuncType(funcName.NAME(0).getText());
        }
        //判断方法名称前面的是 . 还是 :
        TerminalNode terminalNode = (TerminalNode) nodes.get(nodes.size() - 2);
        List<String> tablePrefix = new ArrayList<>();
        for (int i = 0; i < funcName.NAME().size() - 1; i++) {
            tablePrefix.add(funcName.NAME(i).getText());
        }
        String name = funcName.NAME(funcName.NAME().size() - 1).getText();
        if (".".equals(terminalNode.getText())) {
            return new Statement.TableMethod(tablePrefix, name);
        }
        return new Statement.TableExtendMethod(tablePrefix, name);
    }


    public static Expr.Function parseFuncBody(LuaParser.FuncbodyContext funcbodyContext) {
        Expr.Function functionBody = new Expr.Function();
        if (funcbodyContext.parlist() != null) {
            //处理普通的参数
            if (funcbodyContext.parlist().namelist() != null) {
                List<String> names = parseNameList(funcbodyContext.parlist().namelist());
                functionBody.setParamNames(names);
            }
            //处理 ... 参数
            int nodeSize = funcbodyContext.parlist().children.size();
            if (funcbodyContext.parlist().children.get(nodeSize - 1) instanceof TerminalNode last) {
                if ("...".equals(last.getText())) {
                    functionBody.setHasMultiArg(true);
                }
            }
        }

        functionBody.setBlockStatement((Statement.BlockStatement) parseBlock(funcbodyContext.block()));
        return functionBody;
    }

    public static Statement parseAttnameList(LuaParser.StatContext stat) {
        List<String> varNames = new ArrayList<>();
        stat.attnamelist().NAME().forEach(n -> varNames.add(n.getText()));
        List<Expr> exprs = parseExprList(stat.explist());
        return new Statement.LocalDefineStatement(varNames, exprs);
    }

    public static Statement parseAssignStatement(LuaParser.StatContext stat) {
        LuaParser.VarlistContext varlist = stat.varlist();
        List<Expr> exprs = parseExprList(stat.explist());
        List<Expr> left = new ArrayList<>();
        varlist.var().forEach(var -> left.add(parseVar(var)));
        return new Statement.AssignStatement(left, exprs);
    }


    /**
     * 函数调用 同时是 Statement和 Expr
     */
    public static Expr parseFuncCallExpr(LuaParser.StatContext stat) {
        LuaParser.FunctioncallContext functioncall = stat.functioncall();
        LuaParser.VarOrExpContext varOrExpContext = functioncall.varOrExp();
        Expr funcDesc;
        if (varOrExpContext.var() != null) {
            funcDesc = parseVar(varOrExpContext.var());
        } else {
            funcDesc = parseExpr(varOrExpContext.exp());
        }
        return parseNameAndArgsList(funcDesc, functioncall.nameAndArgs());
    }

    public static Statement parseFuncCall(LuaParser.StatContext stat) {
        return (Statement) parseFuncCallExpr(stat);
    }

    public static Expr parseVar(LuaParser.VarContext var) {
        Expr result;
        if (var.NAME() != null) {
            result = new Expr.NameExpr(var.NAME().getText());
        } else {
            result = parseExpr(var.exp());
        }
        for (LuaParser.VarSuffixContext varSuffix : var.varSuffix()) {
            //处理 nameAndArgs
            for (LuaParser.NameAndArgsContext nameAndArgsContext : varSuffix.nameAndArgs()) {
                result = parseNameAndArgs(result, nameAndArgsContext);
            }
            if (varSuffix.NAME() != null) {
                result = new Expr.DotExpr(result, new Expr.NameExpr(varSuffix.NAME().getText()));
            } else if (varSuffix.exp() != null) {
                result = new Expr.IndexExpr(result, parseExpr(varSuffix.exp()));
            }

        }
        return result;
    }

    public static Expr parseNameAndArgs(Expr result, LuaParser.NameAndArgsContext nameAndArgsContext) {
        // a:b() 类型
        if (nameAndArgsContext.NAME() != null) {
            result = new Expr.ColonExpr(result, nameAndArgsContext.NAME().getText());
        }
        //args说明出现了函数调用
        Expr expr = parseArgs(nameAndArgsContext.args());
        List<Expr> args = new ArrayList<>();
        if (expr instanceof Expr.ExprList) {
            args.addAll(((Expr.ExprList) expr).getExprs());
        } else {
            args.add(expr);
        }
        return new Expr.FuncCallExpr(result, args);
    }

    public static Expr parseNameAndArgsList(Expr result, List<LuaParser.NameAndArgsContext> nameAndArgsContexts) {
        for (LuaParser.NameAndArgsContext context : nameAndArgsContexts) {
            result = parseNameAndArgs(result, context);
        }
        return result;
    }

    /**
     * args 函数调用时才会用到
     */
    public static Expr parseArgs(LuaParser.ArgsContext args) {
        if (args.explist() != null) {
            return parseExprList2(args.explist());
        }
        if (args.tableconstructor() != null) {
            return parseTableConstructor(args.tableconstructor());
        }
        if (args.string() != null) {
            return parseString(args.string());
        }
        return new Expr.EmptyArg();
    }

    public static StringValue parseString(LuaParser.StringContext str) {
        if (str.NORMALSTRING() != null) {
            String text = str.NORMALSTRING().getText();
            return new StringValue(StringUtil.extractNormalString(text));
        } else if (str.CHARSTRING() != null) {
            String text = str.CHARSTRING().getText();
            return new StringValue(StringUtil.extractNormalString(text));
        } else {
            return new StringValue(StringUtil.extractLongString(str.LONGSTRING().getText()));
        }

    }


    public static Expr parseTableConstructor(LuaParser.TableconstructorContext context) {
        Expr.TableExpr table = new Expr.TableExpr();
        if (context.fieldlist() == null) {
            return table;
        }
        for (LuaParser.FieldContext field : context.fieldlist().field()) {
            if (field.exp().size() == 2) {
                table.addExpr(parseExpr(field.exp(0)), parseExpr(field.exp(1)));
            } else if (field.NAME() != null) {
                table.addExpr(new Expr.NameExpr(field.NAME().getText()), parseExpr(field.exp(0)));
            } else {
                table.addExpr(parseExpr(field.exp(0)));
            }
        }
        return table;
    }


    public static Expr parseExpr(LuaParser.ExpContext exp) {
        if (exp.getStart() != null && exp.children.size() == 1) {
            String text = exp.getStart().getText();
            if ("nil".equals(text)) {
                return NilValue.NIL;
            }
            if ("true".equals(text)) {
                return BoolValue.TRUE;
            }
            if ("false".equals(text)) {
                return BoolValue.FALSE;
            }
            if ("...".equals(text)) {
                return Expr.MultiArg.MULTI_ARG;
            }
        }
        if (exp.string() != null) {
            return parseString(exp.string());
        }
        if (exp.number() != null) {
            return new NumberValue(Float.valueOf(exp.number().getText()));
        }
        if (exp.prefixexp() != null) {
            LuaParser.VarOrExpContext varOrExp = exp.prefixexp().varOrExp();
            Expr prefix;
            if (varOrExp.var() != null) {
                prefix = parseVar(varOrExp.var());
            } else {
                prefix = parseExpr(varOrExp.exp());
            }
            return parseNameAndArgsList(prefix, exp.prefixexp().nameAndArgs());
        }

        if (exp.functiondef() != null) {
            return parseFuncBody(exp.functiondef().funcbody());
        }
        if (exp.tableconstructor() != null) {
            return parseTableConstructor(exp.tableconstructor());
        }
        if (exp.operatorUnary() != null) {
            return new Expr.UnaryExpr(exp.operatorUnary().getText(), parseExpr(exp.exp(0)));
        }
        Expr ex1 = parseExpr(exp.exp(0));
        Expr ex2 = parseExpr(exp.exp(1));
        if (exp.operatorPower() != null) {
            return new Expr.CalExpr("^", ex1, ex2);
        }
        if (exp.operatorMulDivMod() != null) {
            return new Expr.CalExpr(exp.operatorMulDivMod().getText(), ex1, ex2);
        }
        if (exp.operatorAddSub() != null) {
            return new Expr.CalExpr(exp.operatorAddSub().getText(), ex1, ex2);
        }
        if (exp.operatorStrcat() != null) {
            return new Expr.CalExpr(exp.operatorStrcat().getText(), ex1, ex2);
        }
        if (exp.operatorComparison() != null) {
            return new Expr.RelExpr(exp.operatorComparison().getText(), ex1, ex2);
        }
        if (exp.operatorAnd() != null) {
            return new Expr.AndExpr(ex1, ex2);
        }
        if (exp.operatorOr() != null) {
            return new Expr.OrExpr(ex1, ex2);
        }
        if (exp.operatorBitwise() != null) {
            return new Expr.CalExpr(exp.operatorBitwise().getText(), ex1, ex2);
        }
        throw new RuntimeException("不支持的表达式");
    }
}
