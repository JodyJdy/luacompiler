package com.jdy.lua.executor;

import lombok.Data;

/**
 * label的位置
 */
@Data
public class LabelLocation {
    private final int blockLevel;
    /**
     * label在 block中的下标
     */
    private final int statementIndex;

    public LabelLocation(int blockLevel, int statementIndex) {
        this.blockLevel = blockLevel;
        this.statementIndex = statementIndex;
    }
}
