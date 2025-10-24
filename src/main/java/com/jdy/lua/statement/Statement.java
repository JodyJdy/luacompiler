package com.jdy.lua.statement;

import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Executor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import static com.jdy.lua.statement.StatementTypeEnum.*;

/**
 * @author jdy
 * @title: Statement
 * @description:
 * @data 2023/9/14 16:50
 */
public  interface Statement {

    void visitStatement(Executor visitor);

    StatementTypeEnum statementType();

    class EmptyStatement implements Statement{
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

        @Override
        public StatementTypeEnum statementType() {
            return StatementTypeEnum.EmptyStatement;
        }
    }


    /**
     * local x,y,z = 1,2,3
     * local变量定义
     *
     */
    @Data
    class LocalDefineStatement implements Statement{
        private List<String> varNames;

        public LocalDefineStatement(List<String> varNames, List<Expr> exprs) {
            this.varNames = varNames;
            this.exprs = exprs;
        }

        private List<Expr> exprs;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

        @Override
        public StatementTypeEnum statementType() {
            return LocalDefineStatement;
        }


    }
    /**
     * 赋值语句
     */
    @Data
    class AssignStatement  implements Statement{
        private final List<Expr> left;
        private final List<Expr> right;

        @Override
        public StatementTypeEnum statementType() {
            return AssignStatement;
        }

        public AssignStatement(List<Expr> left, List<Expr> right) {
            this.left = left;
            this.right = right;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    /**
     * while语句
     */
    @Data
    class  WhileStatement implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return WhileStatement;
        }

        private final Expr condition;
        private final BlockStatement blockStatement;

        public WhileStatement(Expr condition, BlockStatement blockStatement) {
            this.condition = condition;
            this.blockStatement = blockStatement;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }


    @Data
    class BlockStatement implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return BlockStatement;
        }

        public BlockStatement() {
        }
        private final List<Statement> statements = new ArrayList<>();

        public void addStatement(Statement statement){
            statements.add(statement);
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    /**
     * 数值for循环
     * for var=exp1,exp2,exp3 do
     *     <执行体>
     * end
     */
    @Data
    class NumberForStatement implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return NumberForStatement;
        }

        private String var;
        private Expr expr1;
        private Expr expr2;
        private Expr expr3;
        private BlockStatement blockStatement;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    /**
     * 泛型for循环
     *for i, v in ipairs(a) do
     *     print(i, v)
     * end
     */
    @Data
    class GenericForStatement implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return GenericForStatement;
        }

        /**
         * 变量名称
         */
        private List<String> vars;
        /**
         * 被遍历的对象
         */
        private List<Expr> expList;

        private BlockStatement blockStatement;

        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    /**
     * repeat
     *    statements
     * until( condition )
     */
    @Data
    class RepeatStatement implements Statement{
        private Expr condition;

        @Override
        public StatementTypeEnum statementType() {
            return RepeatStatement;
        }

        public RepeatStatement(Expr condition, BlockStatement blockStatement) {
            this.condition = condition;
            this.blockStatement = blockStatement;
        }

        private BlockStatement blockStatement;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }
    @Data
    class  BreakStatement  implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return BreakStatement;
        }

        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }
    @Data
    class  ContinueStatement  implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return null;
        }

        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }
    }
    @Data
    class GotoLabelStatement implements Statement{
        public GotoLabelStatement(String labelName) {
            this.labelName = labelName;
        }
        private String labelName;

        @Override
        public StatementTypeEnum statementType() {
            return GotoLabelStatement;
        }

        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }
    }
    @Data
    class LabelStatement implements Statement{
        public LabelStatement(String labelName) {
            this.labelName = labelName;
        }

        private String labelName;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

        @Override
        public StatementTypeEnum statementType() {
            return LabelStatement;
        }
    }
    @Data
    class  IfStatement implements Statement{
        Expr ifCond;
        BlockStatement ifBlock;

        @Override
        public StatementTypeEnum statementType() {
            return IfStatement;
        }

        List<Expr> elseIfConds;
        List<BlockStatement> elseIfBlocks;
        BlockStatement elseBlock;
        public IfStatement(Expr ifCond, BlockStatement ifBlock, List<Expr> elseIfConds, List<BlockStatement> elseIfBlocks, BlockStatement elseBlock) {
            this.ifCond = ifCond;
            this.ifBlock = ifBlock;
            this.elseIfConds = elseIfConds;
            this.elseIfBlocks = elseIfBlocks;
            this.elseBlock = elseBlock;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }
    @Data
    class ReturnStatement implements Statement {
        @Override
        public StatementTypeEnum statementType() {
            return ReturnStatement;
        }

        List<Expr> exprs;

        public ReturnStatement(List<Expr> exprs) {
            this.exprs = exprs;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    /**
     * 函数名称可能有多个类型
     */
    interface FuncType{

    }

    /**
     * 基础的函数类型
     *   function xxx()
     *
     */
    @Data
    class BasicFuncType implements FuncType{
        private String funcName;

        public BasicFuncType(String funcName) {
            this.funcName = funcName;
        }
    }


    @Data
    class TableMethod implements FuncType{
        /**
         * 表名
         *  function a.b.c.d.xx(){
         * }
         */
        private final List<String> tableNames;
        /**
         * 方法名
         */
        private final String methodName;

        public TableMethod(List<String> tableNames, String methodName) {
            this.tableNames = tableNames;
            this.methodName = methodName;
        }
    }

    /**
     * a.b.c:()
     */
    @Data
    class TableExtendMethod implements FuncType{
        public TableExtendMethod(List<String> fatherTableNames, String methodName) {
            this.fatherTableNames = fatherTableNames;
            this.methodName = methodName;
        }

        /**
         * 父类表名称
         */
        private List<String> fatherTableNames;
        /**
         * 方法名称
         */
        private String methodName;
    }

    /**
     * 函数本身也是个表达式
     * a = function(xx){
     * }
     */
    @Data
    class FunctionStatement implements Statement {
        private FuncType funcName;

        @Override
        public StatementTypeEnum statementType() {
            return FunctionStatement;
        }

        public FunctionStatement(FuncType funcName, Expr.LuaFunctionBody funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }

        private Expr.LuaFunctionBody funcBody;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }



    @Data
    class LocalFunctionStatement implements Statement{
        @Override
        public StatementTypeEnum statementType() {
            return LocalFunctionStatement;
        }

        private String funcName;
        private Expr.LuaFunctionBody funcBody;

        public LocalFunctionStatement(String funcName, Expr.LuaFunctionBody funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }

    @Data
    class RequireModule implements Statement,Expr{
        private final String moduleName;

        public RequireModule(String moduleName) {
            this.moduleName = moduleName;
        }

        @Override
        public Value visitExpr(Executor visitor) {
            return visitor.executeExpr(this);
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

        @Override
        public ExprTypeEnum exprType() {
            return ExprTypeEnum.RequireModule;
        }

        @Override
        public StatementTypeEnum statementType() {
            return StatementTypeEnum.RequireModule;
        }
    }


}
