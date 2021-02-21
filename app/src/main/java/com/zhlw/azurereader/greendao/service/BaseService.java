package com.zhlw.azurereader.greendao.service;

import android.database.Cursor;

import com.zhlw.azurereader.greendao.GreenDaoManager;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.gen.BookDao;
import com.zhlw.azurereader.greendao.gen.DaoSession;

import java.util.List;

/**
 基本的数据库操作服务
 */
public class BaseService {

    public void addEntity(Object entity){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.insert(entity);//dao session是我们对数据库crud的最根本地方
    }

    public void addEntitys(List<Book> entitys){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        BookDao bookDao = daoSession.getBookDao();
        bookDao.insertOrReplaceInTx(entitys);
    }

    public void updateEntity(Object entity){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.update(entity);
    }

    public void deleteEntity(Object entity){
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        daoSession.delete(entity);
    }

    /**
     * 通过SQL查找
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor selectBySql(String sql, String[] selectionArgs){
        Cursor cursor = null;
        try {
            DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
            cursor = daoSession.getDatabase().rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return cursor;
    }

    /**
     * 执行SQL进行增删改
     * @param sql
     * @param selectionArgs
     */
    public void  rawQuery(String sql, String[] selectionArgs) {
        DaoSession daoSession  = GreenDaoManager.getInstance().getSession();
        Cursor cursor = daoSession.getDatabase().rawQuery(sql,selectionArgs);
    }

}
