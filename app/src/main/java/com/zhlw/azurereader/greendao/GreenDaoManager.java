package com.zhlw.azurereader.greendao;

import com.zhlw.azurereader.application.MyApplication;
import com.zhlw.azurereader.greendao.gen.DaoMaster;
import com.zhlw.azurereader.greendao.gen.DaoSession;
import com.zhlw.azurereader.greendao.utils.MySQLiteOpenHelper;

/**
 * 单例设计模式(懒汉式，线程不安全)
 */
public class GreenDaoManager {
    private static GreenDaoManager instance;
    private static DaoMaster daoMaster;
    private static MySQLiteOpenHelper mySQLiteOpenHelper;

    public static GreenDaoManager getInstance() {
        if (instance == null) {
            instance = new GreenDaoManager();
        }
        return instance;
    }

    private GreenDaoManager(){
        mySQLiteOpenHelper = new MySQLiteOpenHelper(MyApplication.getmContext(), "read" , null);
        daoMaster = new DaoMaster(mySQLiteOpenHelper.getWritableDatabase());//可以获取一个用于操作数据库的SQLiteDatabase实例
    }

    public DaoSession getSession(){
       return daoMaster.newSession();//创建数据库了
    }
}
