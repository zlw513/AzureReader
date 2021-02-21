package com.zhlw.azurereader.utils;

import android.widget.Toast;

import com.zhlw.azurereader.application.MyApplication;

public class TextHelper {

    public static void showText(final String text){
        MyApplication.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getApplication(),text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showLongText(final String text){
        MyApplication.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getApplication(),text, Toast.LENGTH_LONG).show();
            }
        });
    }
}