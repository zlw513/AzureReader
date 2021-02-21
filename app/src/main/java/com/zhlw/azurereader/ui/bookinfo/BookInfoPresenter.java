package com.zhlw.azurereader.ui.bookinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.read.ReadActivity;
import com.zhlw.azurereader.utils.StringHelper;
import com.zhlw.azurereader.utils.TextHelper;

public class BookInfoPresenter implements BasePresenter {

    private BookInfoActivity mBookInfoActivity;
    private Book mBook;
    private BookService mBookService;
    private boolean isExist = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                mBookInfoActivity.getTvBookDesc().setText(mBook.getDesc());
                mBookInfoActivity.getTvBookType().setText(mBook.getType());
                mBookInfoActivity.getTvBookAuthor().setText(mBook.getAuthor());
            }
        }
    };

    public BookInfoPresenter(BookInfoActivity bookInfoActivity){
        mBookInfoActivity  = bookInfoActivity;
        mBookService = BookService.getInstance();
    }

    @Override
    public void start() {
        mBook = (Book) mBookInfoActivity.getIntent().getSerializableExtra(APPCONST.BOOK);//获取来自search activity的数据
        init();
    }

    private void init(){
        mBookInfoActivity.getTvTitleText().setText(mBook.getName());
        mBookInfoActivity.getTvBookAuthor().setText(mBook.getAuthor());
        if (mBook.getType() == null || "".equals(mBook.getType())){
            //获取书籍描述的方法,根据书名来反查相关信息
            CommonApi.searchDeatil(mBook.getName(), new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    Book book = (Book) o;
                    mBook.setDesc(book.getDesc());
                    mBook.setType(book.getType());
                    mBook.setAuthor(book.getAuthor());
                    handler.sendMessage(handler.obtainMessage(1));
                }

                @Override
                public void onError(Exception e) {

                }
            });
        } else {
            mBookInfoActivity.getTvBookDesc().setText(mBook.getDesc());
        }
        mBookInfoActivity.getTvBookType().setText(mBook.getType());
        mBookInfoActivity.getTvBookName().setText(mBook.getName());

        //判断是否已经加入了书架   是就显示不追了  否就显示加入书架
        if (isBookCollected()){
            mBookInfoActivity.getBtnAddBookcase().setText("不追了");
        } else {
            mBookInfoActivity.getBtnAddBookcase().setText("加入书架");
        }

        //返回上一级
        mBookInfoActivity.getLlTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBookInfoActivity.finish();
            }
        });

        //点加入书架按钮
        mBookInfoActivity.getBtnAddBookcase().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExist) mBook.setId("");
                if (StringHelper.isEmpty(mBook.getId())){
                    if (!isBookCollected()) mBookService.addBook(mBook);
                    isExist = false;
                    TextHelper.showText("成功加入书架");
                    mBookInfoActivity.getBtnAddBookcase().setText("不追了");
                } else {
                    isExist = true;
                    TextHelper.showText("成功移除书籍");
                    mBookService.deleteBookById(mBook.getId());
                    mBookInfoActivity.getBtnAddBookcase().setText("加入书架");
                }
            }
        });

        //立即阅读的话不会加入到数据库里
        mBookInfoActivity.getBtnReadBook().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mBookInfoActivity, ReadActivity.class);
                intent.putExtra(APPCONST.BOOK,mBook);
                mBookInfoActivity.startActivity(intent);
            }
        });

        Glide.with(mBookInfoActivity)
                .load(mBook.getImgUrl())
                .into(mBookInfoActivity.getIvBookImg());
    }

    private boolean isBookCollected(){
        Book book = mBookService.findBookByAuthorAndName(mBook.getName(),mBook.getAuthor());//书名相同作者相同可以证明是同一本书了
        if (book == null){
            return false;
        } else {
            mBook.setId(book.getId());
            return true;
        }
    }

}
