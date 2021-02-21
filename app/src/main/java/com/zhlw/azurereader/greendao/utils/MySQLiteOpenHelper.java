package com.zhlw.azurereader.greendao.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zhlw.azurereader.greendao.gen.BookDao;
import com.zhlw.azurereader.greendao.gen.ChapterDao;
import com.zhlw.azurereader.greendao.gen.DaoMaster;
import com.zhlw.azurereader.greendao.gen.SearchHistoryDao;
import org.greenrobot.greendao.database.Database;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    private Context mContext;

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        mContext = context;
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //加入你要新建的或者修改的表的信息
        GreenDaoUpgrade.getInstance().migrate(db, BookDao.class, ChapterDao.class, SearchHistoryDao.class);
    }

}
