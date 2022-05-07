package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class SuffixedExp extends Expr{

    private Expr primaryExr;

    private SuffixedContent suffixedContent;



    public SuffixedExp(Expr primaryExr) {
        this.primaryExr = primaryExr;
    }

    public SuffixedExp(Expr primaryExr,SuffixedContent content){
        this.primaryExr = primaryExr;
        this.suffixedContent = content;
    }

    @Getter
    public static class SuffixedContent {
        private NameExpr nameExpr;
        private FuncArgs funcArgs;
        private TableIndex tableIndex;
        private boolean hasDot;
        private boolean hasColon;

        /**
         * primaryExpr.nameExpr
         */
        public SuffixedContent(NameExpr nameExpr){
            this.nameExpr = nameExpr;
            this.hasDot = true;
        }

        /**
         * primaryExpr [ expr ]
         */
        public SuffixedContent(TableIndex tableIndex){
            this.tableIndex = tableIndex;
        }

        /**
         * primaryExpr:name(a,b,c)
         */
        public SuffixedContent(NameExpr nameExpr, FuncArgs funcArgs){
            this.nameExpr = nameExpr;
            this.funcArgs = funcArgs;
            this.hasColon = true;
        }

        /**
         *  primaryExpr(x,x,x)
         */
        public SuffixedContent(FuncArgs funcArgs){
            this.funcArgs = funcArgs;
        }
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }


}
