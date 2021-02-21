package com.zhlw.azurereader.ui;

import android.app.Activity;

import java.util.ArrayList;

/**
 * 对应用全局的activity进行管理的
 */
public class ActivityManage {

    private static ArrayList<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity ){
        activities.remove(activity);
    }

    public static void finishAllActivites() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getActivityByCurrenlyRun(){
        if(activities.size() <= 0){
            return null;
        }
        return activities.get(activities.size() - 1);
    }

}
