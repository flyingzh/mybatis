package com.zf.utils;

/**
 * @author admin
 * @date 2020/2/24 10:07
 * @description
 */
public class StringUtils {

    private StringUtils() {

    }

    /**
     * 字符串是否为空
     *
     * @param arg
     * @return
     */
    public static Boolean isNotEmpty(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.trim().length() == 0) {
            return false;
        }
        return true;
    }

}
