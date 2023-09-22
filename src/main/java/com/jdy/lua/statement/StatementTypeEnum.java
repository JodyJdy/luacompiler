package com.jdy.lua.statement;

/**
 * @author jdy
 * @title: StatementTypeEnum
 * @description:
 * @data 2023/9/22 14:42
 */
public enum StatementTypeEnum {
    BlockStatement, LocalFunctionStatement, LocalDefineStatement,
    WhileStatement, RepeatStatement, FunctionStatement, IfStatement, LabelStatement,
    GotoLabelStatement, BreakStatement, ReturnStatement, AssignStatement, NumberForStatement,
    GenericForStatement, RequireModule, FuncCallExpr,EmptyStatement;
}
