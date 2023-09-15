package com.jdy.lua.statement;

import com.jdy.lua.data.NumberValue;
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

    /**
     * ( exp )
     */

    @Data
    class SingleExpr implements Expr{
        Expr single;
    }

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
        public IndexExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }
    @Data
    class AndExpr implements Expr{
        public AndExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }
    @Data
    class OrExpr implements Expr{
        public OrExpr(Expr left, Expr right) {
            this.left = left;
            this.right = right;
        }

        Expr left;
        Expr right;
    }
    @Data
    class NotExpr implements Expr{
        Expr expr;
    }

    /**
     * a..b
     */
    @Data
    class CatExpr implements Expr{
        Expr left;
        Expr right;
    }

    /**
     * #a
     */
    @Data
    class LengthExpr implements Expr{
        Expr expr;
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
        String op;
        Expr left;
        Expr right;

        public CalExpr(String op, Expr left, Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }
    }

    @Data
    class UnaryExpr implements Expr{
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
    }

    @Data
    class FunctionBody implements Expr{
        /**
         * 普通参数
         */

        private List<String> names;
        /**
         * 结尾有 变长参数  ...这种
         */
        private boolean hasMultiArg;

        private Statement.BlockStatement blockStatement;

    }
    @Data
    class ExprList implements Expr{
        public ExprList(List<Expr> exprs) {
            this.exprs = exprs;
        }

        private List<Expr> exprs;
    }
    @Data
    class VarExpr implements Expr{
        Expr prefix;
        /**
         * 可以为空
         */
        Expr suffix;


        public VarExpr(Expr prefix) {
            this.prefix = prefix;
        }

        public VarExpr(Expr prefix, Expr suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
    @Data
    class NameExpr implements Expr{
        public NameExpr(String name) {
            this.name = name;
        }

        private String name;
    }


    @Data
    class EmptyArg implements Expr {
    }
    @Data
    class MultiArg implements Expr{

    }





}
