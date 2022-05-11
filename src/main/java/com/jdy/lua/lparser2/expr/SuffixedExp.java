package com.jdy.lua.lparser2.expr;

import com.jdy.lua.lcodes2.InstructionGenerator;
import com.jdy.lua.lparser2.TableAccess;
import lombok.Data;
import lombok.Getter;

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
        private StringExpr stringExpr;
        private FuncArgs funcArgs;
        private TableIndex tableIndex;
        private boolean hasDot;
        private boolean hasColon;

        /**
         * primaryExpr.nameExpr
         */
        public SuffixedContent(StringExpr stringExpr){
            this.stringExpr = stringExpr;
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
        public SuffixedContent(StringExpr stringExpr, FuncArgs funcArgs){
            this.stringExpr = stringExpr;
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

    public TableAccess tryTrans2TableAccess(){
        if(suffixedContent == null){
            return null;
        }
        if(suffixedContent.hasDot && suffixedContent.getStringExpr() != null){
            return new TableAccess(primaryExr,suffixedContent.getStringExpr());
        }
        if(suffixedContent.getTableIndex() != null){
            return new TableAccess(primaryExr,suffixedContent.getTableIndex().getExpr());
        }
        return null;
    }

    @Override
    public void generate(InstructionGenerator generator, int a, int n) {
        generator.generate(this,a,n);
    }


}
