package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.GenerateInfo;
import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuffixedExp extends Expr{

    private Expr primaryExr;

    private List<SuffixedExpContent> suffixedExpContentList = new ArrayList<>();



    public SuffixedExp(Expr primaryExr) {
        this.primaryExr = primaryExr;
    }

    public void addSuffixedExpContent(SuffixedExpContent content){
        suffixedExpContentList.add(content);
    }

    @Getter
    public static class SuffixedExpContent{
        private NameExpr nameExpr;
        private FuncArgs funcArgs;
        private TableIndex tableIndex;
        private boolean hasDot;
        private boolean hasColon;

        /**
         * primaryExpr.nameExpr
         */
        public SuffixedExpContent(NameExpr nameExpr){
            this.nameExpr = nameExpr;
            this.hasDot = true;
        }

        /**
         * primaryExpr [ expr ]
         */
        public SuffixedExpContent(TableIndex tableIndex){
            this.tableIndex = tableIndex;
        }

        /**
         * primaryExpr:name(a,b,c)
         */
        public SuffixedExpContent(NameExpr nameExpr,FuncArgs funcArgs){
            this.nameExpr = nameExpr;
            this.funcArgs = funcArgs;
            this.hasColon = true;
        }

        /**
         *  primaryExpr(x,x,x)
         */
        public SuffixedExpContent(FuncArgs funcArgs){
            this.funcArgs = funcArgs;
        }
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator) {
       return  generator.generate(this);
    }

    @Override
    public GenerateInfo generate(InstructionGenerator generator, GenerateInfo info) {
        return generator.generate(this,info);
    }


}
