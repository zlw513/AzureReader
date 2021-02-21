package com.zhlw.azurereader.enums;

/**
 * 阅读模式
 */
public enum ReadStyle {

    protectedEye,//护眼
    common,//普通
    blueDeep,//深蓝
    leather,//羊皮纸
    breen;//

    ReadStyle() {

    }

    public static ReadStyle get(int var0) {
        return values()[var0];
    }

    public static ReadStyle fromString(String string) {
        return ReadStyle.valueOf(string);
    }
}
