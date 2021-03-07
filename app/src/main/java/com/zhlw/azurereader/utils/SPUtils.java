package com.zhlw.azurereader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.SharedPreferencesCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SPUtils {

    private SPUtils(){}

    /**
     * 默认的SharePreference名称
     */
    private static final String SHARED_NAME = "colorselect";

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param context    应用程序上下文
     * @param key        key关键字
     * @param defaultObject    默认值
     * @return    返回获取的String值
     */
    public static Object get(Context context, String key, Object defaultObject){
        SharedPreferences sp = getSharedPreferences(context);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * @param context    应用程序上下文
     * @param key         key关键字
     * @param object    对应值
     * @return 成功返回true，失败返回false
     */
    public static void put(Context context, String key, Object object){
        if (object instanceof String) {
            getEditor(context).putString(key, (String) object);
        } else if (object instanceof Integer) {
            getEditor(context).putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            getEditor(context).putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            getEditor(context).putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            getEditor(context).putLong(key, (Long) object);
        } else {
            getEditor(context).putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(getEditor(context));
    }

    /**
     * 获取Editor对象
     * @param context    应用程序上下文
     * @return    返回Editor对象
     */
    private static SharedPreferences.Editor getEditor(Context context){
        return getSharedPreferences(context).edit();
    }

    /**
     * 获取SharedPreferences对象
     * @param context    应用程序上下文
     * @return    返回SharedPreferences对象
     */
    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         * @return
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            editor.commit();
        }
    }

}
