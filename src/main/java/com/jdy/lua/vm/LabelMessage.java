package com.jdy.lua.vm;


import java.io.Serializable;

/**
 * label
 *
 * @param pc      程序指针
 * @param usedReg 使用的寄存器数目
 */
public record LabelMessage(int pc, int usedReg) implements Serializable {

}
