package com.jdy.lua.lparser2.expr;

import java.util.ArrayList;
import java.util.List;

public class TableConstructor  extends Expr{
    // [x]=x
    private List<TableField> fields = new ArrayList<>();
    // x,y,z
    private List<TableListField> listFields = new ArrayList<>();
}
