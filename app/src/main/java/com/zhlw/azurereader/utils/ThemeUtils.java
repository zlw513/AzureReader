package com.zhlw.azurereader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class ThemeUtils {

    private static OnThemeChangeListener mOnThemeChangeListener;
    private static int mThereColor = 0;

    /**
     * 注册ThemeChangeListener
     *
     * @param listener
     */
    public static void registerThemeChangeListener(OnThemeChangeListener listener) {
        mOnThemeChangeListener = listener;
    }

    public static void setThereColor(int thereColor) {
        mThereColor = thereColor;
        mOnThemeChangeListener.onThemeChanged();
    }

    public static int getmThereColor(Context context) {
        if (mThereColor == 0){
            SharedPreferences preferences = context.getSharedPreferences("colorselect", MODE_PRIVATE);
            int color = preferences.getInt("colorselect",Color.parseColor("#008577"));
//            int color = (int) SPUtils.get(context, "colorselect", Integer.valueOf(Color.parseColor("#008577")));
            mThereColor = color;
            return color;
        } else {
            return mThereColor;
        }
    }

    /**
     * 主题模式切换监听器
     */
    public interface OnThemeChangeListener {
        /**
         * 主题切换时回调
         */
        void onThemeChanged();
    }

}
