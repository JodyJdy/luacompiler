package com.jdy.lua.statement;

/**
 * @author jdy
 * @title: ExprTypeEnum
 * @description:
 * @data 2023/9/22 14:42
 */
public enum ExprTypeEnum {
    NILValue, BoolValue, IndexExpr, StringValue, NumberValue,
    TableExpr, Function, CalExpr, RelExpr, NameExpr, AndExpr,
    OrExpr, DotExpr, FuncCallExpr, ColonExpr, MultiArg, UnaryExpr,
    RequireModule, CatExpr, EmptyArg;
}
