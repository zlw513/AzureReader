package com.zhlw.azurereader.utils;

import android.content.Context;
import android.widget.Toast;

import com.zhlw.azurereader.application.MyApplication;

/**
 * Created by Chu on 2016/3/14.
 */
public class ToastUtils {
    private static Toast sToast = null;
    public static boolean isShow = true;

    public static void showToast(Context context ,String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToast(String msg) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(MyApplication.getmContext(), msg, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToast(int resId) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(MyApplication.getmContext(), resId, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(resId);
        }
        sToast.show();
    }

    public static void showToastLong( String msg) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(MyApplication.getmContext(), msg, Toast.LENGTH_LONG);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    public static void showToastLong(int resId) {
        if (!isShow) {
            return;
        }
        if (sToast == null) {
            sToast = Toast.makeText(MyApplication.getmContext(), resId, Toast.LENGTH_LONG);
        } else {
            sToast.setText(resId);
        }
        sToast.show();
    }

    // 主要针对需要在某个时候，取消提示
    public static void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}
