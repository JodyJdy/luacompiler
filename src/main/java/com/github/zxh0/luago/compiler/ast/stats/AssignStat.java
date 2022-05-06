package com.github.zxh0.luago.compiler.ast.stats;

import com.github.zxh0.luago.compiler.ast.Exp;
import com.github.zxh0.luago.compiler.ast.Stat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignStat extends Stat {

    private List<Exp> varList;
    private List<Exp> expList;

    public AssignStat(int lastLine,
                      List<Exp> varList, List<Exp> expList) {
        setLastLine(lastLine);
        this.varList = varList;
        this.expList = expList;
    }

}
