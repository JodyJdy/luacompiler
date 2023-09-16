package com.jdy.lua.executor;

import lombok.Data;

/**
 * label的位置
 */
@Data
public class LabelLocation {
    private final int blockLevel;
    private final int statmentIndex;

    public LabelLocation(int blockLevel, int statmentIndex) {
        this.blockLevel = blockLevel;
        this.statmentIndex = statmentIndex;
    }
}
