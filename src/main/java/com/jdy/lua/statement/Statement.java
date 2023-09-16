package com.jdy.lua.statement;

import com.jdy.lua.executor.Executor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jdy
 * @title: Statement
 * @description:
 * @data 2023/9/14 16:50
 */
public  interface Statement {

    void visitStatement(Executor visitor);

    class EmptyStatement implements Statement{
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
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


    }
    /**
     * 赋值语句
     */
    @Data
    class AssignStatement  implements Statement{
        private final List<Expr> left;
        private final List<Expr> right;

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
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }
    @Data
    class  ContinueStatement  implements Statement{
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
    }
    @Data
    class  IfStatement implements Statement{
        Expr ifCond;
        BlockStatement ifBlock;
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


    /**
     * 表的方法
     * Account = {balance = 0}
     * function Account.withdraw (v)
     *     Account.balance = Account.balance - v
     * end
     */
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
     * 表的派生类方法
     * -- 元类
     * Rectangle = {area = 0, length = 0, breadth = 0}
     * -- 派生类的方法 new
     * function Rectangle:new (o,length,breadth)
     *   o = o or {}
     *   setmetatable(o, self)
     *   self.__index = self
     *   self.length = length or 0
     *   self.breadth = breadth or 0
     *   self.area = length*breadth;
     *   return o
     * end
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
     *
     * }
     */
    @Data
    class FunctionStatement implements Statement {
        private FuncType funcName;

        public FunctionStatement(FuncType funcName, Expr.Function funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }

        private Expr.Function funcBody;
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }



    @Data
    class LocalFunctionStatement implements Statement{
        private String funcName;
        private Expr.Function funcBody;

        public LocalFunctionStatement(String funcName, Expr.Function funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }
        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

    }


}
