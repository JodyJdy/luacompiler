package com.jdy.lua.statement;

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

    class EmptyStatement implements Statement{
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

    }
    /**
     * 赋值语句
     */
    @Data
    class AssignStatement  implements Statement{
        Boolean isLocal;
        private final List<Expr> left;
        private final List<Expr> right;

        public AssignStatement(List<Expr> left, List<Expr> right) {
            this.left = left;
            this.right = right;
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
    }


    @Data
    class BlockStatement implements Statement{
        public BlockStatement() {
        }
        private final List<Statement> statements = new ArrayList<>();

        private Statement lastStatement;

        public void addStatement(Statement statement){
            statements.add(statement);
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
    }
    @Data
    class  BreakStatement  implements Statement{
    }
    @Data
    class  ContinueStatement  implements Statement{
    }
    @Data
    class GotoLabelStatement implements Statement{
        public GotoLabelStatement(String labelName) {
            this.labelName = labelName;
        }

        private String labelName;
    }
    @Data
    class LabelStatement implements Statement{
        public LabelStatement(String labelName) {
            this.labelName = labelName;
        }

        private String labelName;

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
    }
    class ReturnStatement implements Statement {
        List<Expr> exprs;

        public ReturnStatement(List<Expr> exprs) {
            this.exprs = exprs;
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
         *
         * }
         */
        private List<String> tableNames;
        /**
         * 方法名
         */
        private String methodName;

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

        public FunctionStatement(FuncType funcName, Expr.FunctionBody funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }

        private Expr.FunctionBody funcBody;
    }



    @Data
    class LocalFunctionStatement implements Statement,Expr{
        private String funcName;
        private FunctionBody funcBody;

        public LocalFunctionStatement(String funcName, FunctionBody funcBody) {
            this.funcName = funcName;
            this.funcBody = funcBody;
        }
    }


}
