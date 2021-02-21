package com.zhlw.azurereader.greendao.service;

import android.database.Cursor;

import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.GreenDaoManager;
import com.zhlw.azurereader.greendao.gen.BookDao;
import com.zhlw.azurereader.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * 操作书籍数据库服务
 */
public class BookService extends BaseService {
    private static BookService mBookService;
    private ChapterService mChapterService;

    private BookService(){
        mChapterService = new ChapterService();
    }

    public static BookService getInstance(){
        if (mBookService == null){
            synchronized (BookService.class){
                if (mBookService == null) mBookService = new BookService();
            }
        }
        return mBookService;
    }

    private List<Book> findBooks(String sql, String[] selectionArgs) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            Cursor cursor = selectBySql(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Book book = new Book();
                book.setId(cursor.getString(0));
                book.setName(cursor.getString(1));
                book.setChapterUrl(cursor.getString(2));
                book.setImgUrl(cursor.getString(3));
                book.setDesc(cursor.getString(4));
                book.setAuthor(cursor.getString(5));
                book.setType(cursor.getString(6));
                book.setUpdateDate(cursor.getString(7));
                book.setNewestChapterId(cursor.getString(8));
                book.setNewestChapterTitle(cursor.getString(9));
                book.setNewestChapterUrl(cursor.getString(10));
                book.setHistoryChapterId(cursor.getString(11));
                book.setHisttoryChapterNum(cursor.getInt(12));
                book.setSortCode(cursor.getInt(13));
                book.setNoReadNum(cursor.getInt(14));
                book.setChapterTotalNum(cursor.getInt(15));
                book.setLastReadPosition(cursor.getInt(16));
                book.setIsLocal(cursor.getInt(17) > 0);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 通过ID查书
     * @param id
     * @return
     */
    public Book getBookById(String id) {
        BookDao bookDao = GreenDaoManager.getInstance().getSession().getBookDao();
        return bookDao.load(id);
    }

    /**
     * 获取所有的书
     * @return
     */
    public List<Book> getAllBooks(){
        String sql = "select * from book order by sort_code";
        return findBooks(sql, null);
    }

    /**
     * 新增书  书的id是在这时才确定的
     * @param book
     */
    public void addBook(Book book){
        book.setSortCode(countBookTotalNum() + 1);//设置排序码  默认为当前书籍总数 + 1
        book.setId(StringHelper.getStringRandom(25));//获取长25位的id号  相同概率为1 / 25^9   近4万亿分之一
        addEntity(book);
    }

    /*
     * 添加一堆书，原理同上
     */
    public void addBooks(List<Book> books){
        for (Book book:books){
            book.setSortCode(countBookTotalNum() + 1);//设置排序码  默认为当前书籍总数 + 1
            book.setId(StringHelper.getStringRandom(25));//获取长25位的id号  相同概率为1 / 25^9   近4万亿分之一
        }
        addEntitys(books);
    }

    /**
     * 查询是否本地书籍
     * @param  url 本地书籍地址
     * @return
     */
    public Book getLocalBookUrl(String url){
        Book book = null;
        Cursor cursor = null;
        try {
            cursor = selectBySql("select id from book where CHAPTER_URL = ?",new String[]{url});
            if (cursor != null && cursor.moveToNext()){
                String id = cursor.getString(0);
                book = getBookById(id);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return book;
    }

    /**
     * 查找书（作者、书名）
     * @param author
     * @param bookName
     * @return
     */
    public Book findBookByAuthorAndName(String bookName, String author){
        Book book = null;
        Cursor cursor = null;
        try {
            cursor = selectBySql("select id from book where author = ? and name = ?",new String[]{author,bookName});
            if (cursor.moveToNext()){
                String id = cursor.getString(0);
                book = getBookById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return book;
    }

    /**
     * 删除书
     * @param id
     */
    public void deleteBookById(String id){
        BookDao bookDao = GreenDaoManager.getInstance().getSession().getBookDao();
        bookDao.deleteByKey(id);
        mChapterService.deleteBookALLChapterById(id);
    }

    /**
     * 删除书
     * @param book
     */
    public void deleteBook(Book book){
        deleteEntity(book);
        mChapterService.deleteBookALLChapterById(book.getId());
    }

    /**
     * 查询书籍总数  改了2020-22点04
     * @return
     */
    public int countBookTotalNum(){
        int num = 0;
        Cursor cursor = null;
        try {
            cursor = selectBySql("select count(*) n from book ",null);
            if (cursor.moveToNext()){
                num = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return num;
    }

    /**
     * 更新书
     * @param books
     */
    public void updateBooks(List<Book> books){
        BookDao bookDao = GreenDaoManager.getInstance().getSession().getBookDao();
        bookDao.updateInTx(books);
    }

}
