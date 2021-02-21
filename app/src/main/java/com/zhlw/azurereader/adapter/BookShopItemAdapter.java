package com.zhlw.azurereader.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 书城的item 的adapter
 */
public class BookShopItemAdapter extends BaseQuickAdapter<Book,BaseViewHolder> {

    public BookShopItemAdapter(int layoutResId, @Nullable List<Book> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Book book) {
        if (StringHelper.isEmpty(book.getImgUrl())){
            book.setImgUrl("");
        }
        Glide.with(mContext)
                .load(book.getImgUrl())
                .apply(new RequestOptions().error(R.drawable.no_image).placeholder(R.drawable.no_image))
                .into((ImageView) baseViewHolder.getView(R.id.iv_book_img));
        baseViewHolder.setText(R.id.tv_book_name,book.getName());
        baseViewHolder.setVisible(R.id.iv_delete,false);//将那个删除图标去掉
    }
}
