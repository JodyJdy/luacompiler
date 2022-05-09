package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.ExprList;
import com.jdy.lua.lparser2.expr.NameExpr;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class LocalStatement extends Statement{
    List<NameExpr> nameExprList = new ArrayList<>();
    Map<Integer,String> attributes = new HashMap<>();
    private ExprList exprList;


    public void addNameExpr(NameExpr nameExpr){
        nameExprList.add(nameExpr);
    }
    public void addNameExprAttributes(int index,String attr){
        attributes.put(index,attr);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }


}
