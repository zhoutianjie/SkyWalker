package com.kedacom.baseutil;

/**
 * Created by zhoutianjie on 2018/11/19.
 */

public class StringUtil {

    /**
     * 字符串经过trim处理之后是否为空
     *
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        boolean b = false;
        if (str == null || str.trim().length() == 0) b = true;

        return b;
    }
}
