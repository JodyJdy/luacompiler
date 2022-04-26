package com.jdy.lua.lparser;

import com.jdy.lua.lex.LexState;
import com.jdy.lua.lobjects.Proto;
import lombok.Data;

@Data
public class FuncState {
    Proto f;
    FuncState prev;
    LexState lexState;
    BlockCnt blockCnt;
    int pc;  /* next position to code (equivalent to 'ncode') */
    int lasttarget;   /* 'label' of last 'jump label' */
    int previousline;  /* last line that was saved in 'lineinfo' */
    int nk;  /* number of elements in 'k' */
    int np;  /* number of elements in 'p' */
    int nabslineinfo;  /* number of elements in 'abslineinfo' */
    int firstlocal;  /* index of first local var (in Dyndata array) */
    int firstlabel;  /* index of first label (in 'dyd->label->arr') */
    short ndebugvars;  /* number of elements in 'f->locvars' */
    int nactvar;  /* number of active local variables */
    int nups;  /* number of upvalues */
    /*当前函数栈的下一个可用位置*/
    int freereg;  /* first free register */
    int iwthabs;  /* instructions issued since last absolute line info */
    int needclose;  /* function needs to close upvalues when returning */

}
