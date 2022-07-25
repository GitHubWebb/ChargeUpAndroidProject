package com.hjq.base.util;

import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-14 22:20:59
 *      description:    View工具类
 *      version:        v1.0
 * </pre>
 */
public class ViewUtils {

    /** 获取 TV 文本内容 */
    public static String getStringText(TextView et) {
        return getStringText(et,true);
    }

    /** 获取 TV 文本内容 isTrim true 去除前后空格 */
    @NonNull
    public static String getStringText(TextView et , boolean isTrim) {
        if (et == null )
            return "";

        String viewStr = et.getText().toString();
        if (isTrim)
            return viewStr.trim();
        else
            return viewStr;
    }

}
