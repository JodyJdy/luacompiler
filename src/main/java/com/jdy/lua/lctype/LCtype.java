package com.jdy.lua.lctype;

import com.sun.crypto.provider.HmacSHA1KeyGenerator;

public class LCtype {
    public static int ALPHABIT = 0;
    public static int DIGITBIT = 1;
    public static int PRINTBIT = 2;
    public static int SPACEBIT = 3;
    public static int XDIGITBIT = 4;

    public static int NONA = 0x00;


    public static int luai_ctype_[] = {
        0x00,  /* EOZ */
                0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,	/* 0. */
                0x00,  0x08,  0x08,  0x08,  0x08,  0x08,  0x00,  0x00,
                0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,	/* 1. */
                0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,
                0x0c,  0x04,  0x04,  0x04,  0x04,  0x04,  0x04,  0x04,	/* 2. */
                0x04,  0x04,  0x04,  0x04,  0x04,  0x04,  0x04,  0x04,
                0x16,  0x16,  0x16,  0x16,  0x16,  0x16,  0x16,  0x16,	/* 3. */
                0x16,  0x16,  0x04,  0x04,  0x04,  0x04,  0x04,  0x04,
                0x04,  0x15,  0x15,  0x15,  0x15,  0x15,  0x15,  0x05,	/* 4. */
                0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,
                0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,	/* 5. */
                0x05,  0x05,  0x05,  0x04,  0x04,  0x04,  0x04,  0x05,
                0x04,  0x15,  0x15,  0x15,  0x15,  0x15,  0x15,  0x05,	/* 6. */
                0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,
                0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,  0x05,	/* 7. */
                0x05,  0x05,  0x05,  0x04,  0x04,  0x04,  0x04,  0x00,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* 8. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* 9. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* a. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* b. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                0x00,  0x00,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* c. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* d. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,	/* e. */
                NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,  NONA,
                NONA,  NONA,  NONA,  NONA,  NONA,  0x00,  0x00,  0x00,	/* fJmp. */
                0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00
    };


    public static int mask(int b){
        return 1 << b;
    }

    public static boolean testprop(int c,int p){
        return (luai_ctype_[c + 1] & p) != 0;
    }

    public static boolean isAlpha(int c){
        return testprop(c, mask(ALPHABIT));
    }
    public static boolean  isalNum(int c){
        return testprop(c, mask(ALPHABIT) | mask(DIGITBIT));
    }
    public static boolean  isDigit(int c){
        return testprop(c, mask(DIGITBIT));
    }
    public static boolean  isSpace(int c){
        return testprop(c, mask(SPACEBIT));
    }

    public static boolean  isPrint(int c){
        return testprop(c, mask(PRINTBIT));
    }

    public static boolean  isHexDigit(int c){
        return testprop(c, mask(XDIGITBIT));
    }

}
