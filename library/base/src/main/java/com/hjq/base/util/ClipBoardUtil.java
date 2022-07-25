package com.hjq.base.util;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-15 17:07:17
 *      description:    剪切板操作工具类
 *      version:        v1.0
 * </pre>
 */
public class ClipBoardUtil {

    /** 获取剪切板内容 */
    public static String paste(Activity mActivity) {

        ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();
                String addedTextString = String.valueOf(addedText);
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString;
                }
            }

        }

        return "";

    }

    /**  清空剪切板 */
    public static void clear(Activity mActivity) {

        ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(manager.getPrimaryClip());
                manager.setPrimaryClip(ClipData.newPlainText("", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}