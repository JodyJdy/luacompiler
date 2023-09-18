package com.jdy.lua.vm;

import com.jdy.lua.statement.Expr;
import com.jdy.lua.statement.Statement;
import static com.jdy.lua.vm.ByteCode.*;

/**
 *
 * 用于生成字节码指令
 * @author jdy
 * @title: InstructionGenerator
 * @description:
 * @data 2023/9/18 14:15
 */
public class InstructionGenerator {

    private Block currentBlock;

    public InstructionGenerator(Block currentBlock, Statement.BlockStatement statement) {
        this.currentBlock = currentBlock;
    }


    public void generateStatement(){

    }

    /**
     * 返回使用的寄存器数目
     */
    public int generateExpr(Expr expr) {
        int count = 0;
        if (expr instanceof Expr.NameExpr nameExpr) {
            int reg =currentBlock.allocRegister();
            StackElement elem = currentBlock.searchVar(nameExpr.getName());
            if (elem != null) {
                System.out.println(new LOADCONSTANT(reg,reg));
                System.out.println(new LOADVAR(reg,reg));
            }
        }
        return count;
    }
}
