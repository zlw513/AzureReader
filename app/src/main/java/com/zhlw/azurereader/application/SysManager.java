package com.zhlw.azurereader.application;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.bean.Setting;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.enums.Font;
import com.zhlw.azurereader.enums.Language;
import com.zhlw.azurereader.enums.ReadStyle;
import com.zhlw.azurereader.utils.CacheHelper;


public class SysManager {

    public static void logout() {

    }

    /**
     * 获取设置
     * @return
     */
    public static Setting getSetting() {
        Setting setting = (Setting) CacheHelper.readObject(APPCONST.FILE_NAME_SETTING);
        if (setting == null) {
            setting = getDefaultSetting();
            saveSetting(setting);
        }
        return setting;
    }

    /**
     * 保存设置
     * @param setting
     */
    public static void saveSetting(Setting setting) {
        CacheHelper.saveObject(setting, APPCONST.FILE_NAME_SETTING);
    }

    /**
     * 默认设置
     * @return
     */
    private static Setting getDefaultSetting(){
        Setting setting = new Setting();
        setting.setDayStyle(true);
        setting.setReadBgColor(R.color.sys_protect_eye_bg);
        setting.setReadStyle(ReadStyle.protectedEye);
        setting.setReadWordSize(20);
        setting.setReadWordColor(R.color.sys_protect_eye_word);
        setting.setBrightProgress(50);
        setting.setBrightFollowSystem(true);
        setting.setLanguage(Language.simplified);
        setting.setFont(Font.默认字体);
        setting.setAutoScrollSpeed(50);
        return setting;
    }


}
