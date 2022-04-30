package com.jdy.lua.lparser;

import lombok.Data;

/**
 * 存储表构建时的参数
 */
@Data
public class TableConstructor {
    //表初始化的数据
    ExpDesc v = new ExpDesc();
    //描述表
    ExpDesc t;
    //hash table size
    int nh;
    //已经存储的数组部分
    int na;
    //等待存储的数据的数量
    int toStore;
}
