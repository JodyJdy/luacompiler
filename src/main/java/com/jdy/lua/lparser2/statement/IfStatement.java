package com.jdy.lua.lparser2.statement;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.expr.Expr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IfStatement extends Statement{
    private Expr cond;
    private BlockStatement blockStatement;

    private List<Expr> elseThenConds = new ArrayList<>();
    private List<BlockStatement> elseThenBlock = new ArrayList<>();

    private BlockStatement elseBlock;


    public IfStatement(Expr cond, BlockStatement blockStatement) {
        this.cond = cond;
        this.blockStatement = blockStatement;
    }

    public void addElseThenCond(Expr cond){
        this.elseThenConds.add(cond);
    }
    public void addElseThenBlock(BlockStatement block){
        this.elseThenBlock.add(block);
    }

    @Override
    public void generate(InstructionGenerator ins) {
        ins.generate(this);
    }

    /**
     * 获取所有的cond
     */
    public List<Expr> getAllConds(){
        List<Expr> exprs = new ArrayList<>();
        exprs.add(cond);
        exprs.addAll(elseThenConds);
        return exprs;
    }

    /**
     * 获取除了 else block以外的所有block
     */
    public List<BlockStatement> getAllIfBlock(){
        List<BlockStatement> blockStatements = new ArrayList<>();
        blockStatements.add(blockStatement);
        blockStatements.addAll(elseThenBlock);
        return blockStatements;
    }
}
