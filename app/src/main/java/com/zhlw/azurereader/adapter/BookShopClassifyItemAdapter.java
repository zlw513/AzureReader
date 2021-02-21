package com.zhlw.azurereader.adapter;

import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.utils.StringHelper;

import java.util.List;

/**
 * 书城的item 的adapter
 */
public class BookShopClassifyItemAdapter extends BaseQuickAdapter<Book,BaseViewHolder> {

    BookService mBookService;

    public BookShopClassifyItemAdapter(int layoutResId, @Nullable List<Book> data) {
        super(layoutResId, data);
        mBookService = BookService.getInstance();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Book book) {
        baseViewHolder.setText(R.id.tv_bookname_bookshop_more,book.getName());
        baseViewHolder.setText(R.id.tv_bookname_bookshop_more_author, book.getAuthor() == null ? "作者不详" : book.getAuthor());
        if (isBookCollected(book.getName(), book.getAuthor())) {
            baseViewHolder.setText(R.id.tv_action_add_book, "已在书架");
            baseViewHolder.setEnabled(R.id.tv_action_add_book,false);
        } else {
            baseViewHolder.setText(R.id.tv_action_add_book, "加入书架");
            baseViewHolder.addOnClickListener(R.id.tv_action_add_book);
            baseViewHolder.setEnabled(R.id.tv_action_add_book,true);
        }
    }

    private boolean isBookCollected(String bookName,String author){
        Book book = mBookService.findBookByAuthorAndName(bookName,author);//书名相同作者相同可以证明是同一本书了
        return book != null;
    }

}
