package com.jdy.lua.statement;

import com.jdy.lua.data.NumberValue;
import com.jdy.lua.data.Value;
import com.jdy.lua.executor.Executor;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jdy
 * @title: Expr
 * @description:
 * @data 2023/9/14 16:51
 */
public interface Expr  {

    Value visitExpr(Executor vistor);



    /**
     *
     * 存在函数调用
     * a:b()
     */
    @Data
    class ColonExpr implements Expr{
        Expr left;

        public ColonExpr(Expr left, String name) {
            this.left = left;
            this.name = name;
        }
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }


        String name;
    }
    /**
     *
     * a.b
     */
    @Data
    class DotExpr implements Expr{
        Expr left;
        Expr right;

        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }


        public DotExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }
    }
    /**
     * 索引
     * a[x]
     */
    @Data
    class IndexExpr implements Expr{

        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        public IndexExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }
    @Data
    class AndExpr implements Expr{

        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        public AndExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }
    @Data
    class OrExpr implements Expr{

        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        public OrExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }

    /**
     * a..b
     */
    @Data
    class CatExpr implements Expr{
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        Expr left;
        Expr right;
    }

    /**
     * 运算
     * +
     * -
     * *
     * /
     * %
     * ^
     * -
     * //
     */
    @Data
    class CalExpr implements Expr{
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        String op;
        Expr left;
        Expr right;

        public CalExpr(String op, Expr left, Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
    }

    /**
     *  # , not  ,- , ~
     */
    @Data
    class UnaryExpr implements Expr{
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        private String op;

        public UnaryExpr(String op, Expr expr) {
            this.op = op;
            this.expr = expr;
        }

        private Expr expr;

        public UnaryExpr(Expr expr) {
            this.expr = expr;
        }
    }

    /**
     * 关系运算符
     * ==
     * ~=
     * >
     * <
     * >=
     * <=
     */
    @Data
    class RelExpr implements Expr{
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        public RelExpr(String op, Expr left, Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        String op;
        Expr left;
        Expr right;
    }

    @Data
    class FuncCallExpr implements Expr,Statement{
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

        @Override
        public void visitStatement(Executor visitor) {
            visitor.executeStatement(this);
        }

        public FuncCallExpr(Expr func, List<Expr> exprs) {
            this.func = func;
            this.exprs = exprs;
        }

        /**
         * 函数描述
         */
        private Expr func;
        private List<Expr> exprs;
    }

    /**
     * 表
     * 虽然是数组， 但是本质是map
     */
    @Data
    class TableExpr implements Expr{
        private Map<Expr, Expr> exprExprMap = new LinkedHashMap<>();


        /**
         * 序号由 exprExprMap的size推导
         * @param right
         */
        public void addExpr(Expr right) {
            exprExprMap.put(new NumberValue(exprExprMap.size()), right);
        }

        public void addExpr(Expr left, Expr right) {
            exprExprMap.put(left, right);
        }
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

    }

    @Data
    class Function implements Expr{
        /**
         * 普通参数
         */

        protected List<String> paramNames;
        /**
         * 结尾有 变长参数  ...这种
         */
        protected boolean hasMultiArg;

        private Statement.BlockStatement blockStatement;

        @Override
        public Value visitExpr(Executor visitor) {
            return visitor.executeExpr(this);
        }

    }
    @Data
    class ExprList implements Expr{
        public ExprList(List<Expr> exprs) {
            this.exprs = exprs;
        }

        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }
        private List<Expr> exprs;
    }
    @Data
    class NameExpr implements Expr{
        public NameExpr(String name) {
            this.name = name;
        }

        private String name;
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

    }


    @Data
    class EmptyArg implements Expr {
        @Override
        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }

    }

    @Data
    class MultiArg implements Expr {
        public static MultiArg MULTI_ARG = new MultiArg();
        private MultiArg(){

        }
        @Override

        public Value visitExpr(Executor vistor) {
            return vistor.executeExpr(this);
        }
    }





}
