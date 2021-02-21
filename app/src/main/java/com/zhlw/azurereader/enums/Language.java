package com.zhlw.azurereader.enums;

/**
 * 简体繁体切换
 */

public enum Language {

    simplified,//简体中文
    traditional;//繁体中文


    Language() {

    }

    public static Language get(int var0) {
        return values()[var0];
    }

    public static Language fromString(String string) {
        return Language.valueOf(string);
    }
}
