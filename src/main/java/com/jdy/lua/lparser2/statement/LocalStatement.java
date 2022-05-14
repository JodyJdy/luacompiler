package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
public class LocalStatement extends Statement{
    List<String> names = new ArrayList<>();
    Map<Integer,String> attributes = new HashMap<>();
    private List<Expr> exprList;


    public void addVarName(String name){
        names.add(name);
    }
    public void addNameExprAttributes(int index,String attr){
        attributes.put(index,attr);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }


    public static class Builder{
        List<String> names = new ArrayList<>();
        List<Expr> exprs = new ArrayList<>();
        public Builder addLocalVar(String name,Expr expr){
            names.add(name);
            exprs.add(expr);
            return this;
        }
        public Builder addVarName(String name){
            this.names.add(name);
            return this;
        }
        public Builder setExprList(List<Expr> exprList){
            this.exprs = exprList;
            return this;
        }
        public LocalStatement build(){
           LocalStatement localStatement = new LocalStatement();
           localStatement.setNames(names);
           localStatement.setExprList(exprs);
           return localStatement;
        }
    }

    public static Builder builder(){
        return new Builder();
    }


}
