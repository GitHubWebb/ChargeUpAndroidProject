package com.hjq.base.util;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 22:24:12
 *      description:    字符串工具类
 *      version:        v1.0
 * </pre>
 */
public class StringUtils {
    public static String emptyNullValue(String val) {
        return emptyNullValue(val,"");
    }

    /**
     * val 值为空时候替换为 defaultValue
     * @param val
     * @param defaultValue
     * @return
     */
    public static String emptyNullValue(String val, String defaultValue) {
        if (TextUtils.isEmpty(val))
            return defaultValue;
        return val.trim();
    }

    //
    public static String emptyNullValue(Long val) {
        if (val == null)
            return "";
        return val.toString();
    }

    //
    public static String emptyNullValue(Boolean val) {
        if (val == null)
            return "";
        return val + "";
    }

    public static String emptyNullValue(BigDecimal val) {
        if (val == null)
            return "";

        return val + "";
    }

    public static String emptyNullValue(Double val) {
        if (val == null)
            return "";
        return val + "";
    }

    public static String emptyNullValue(Integer val) {
        if (val == null)
            return "";
        return val + "";
    }

}
