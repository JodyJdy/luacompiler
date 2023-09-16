package com.jdy.lua.util;

public class StringUtil {
    /**
     * "xx"   ->   xx
     *  'xx'  -> xx
     */
    public static String  extractNormalString(String text) {
        if (text == null || text.length()  < 2) {
            return text;
        }
        return text.substring(1, text.length() - 1);
    }

    /**
     * [[xx]] -> xx

     */
    public static String extractLongString(String text) {
        if (text == null || text.length()  < 4) {
            return text;
        }
        return text.substring(2, text.length() - 2);
    }



}
