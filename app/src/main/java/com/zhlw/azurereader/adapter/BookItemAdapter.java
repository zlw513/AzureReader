package com.zhlw.azurereader.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.utils.StringHelper;
import java.util.List;

public class BookItemAdapter extends BaseItemDraggableAdapter<Book, BaseViewHolder> {

    private Context mContext;

    public BookItemAdapter(int layoutResId, List data, Context context) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Book book) {
        if (StringHelper.isEmpty(book.getImgUrl())){
            book.setImgUrl("");
        }
        Glide.with(mContext)
                .load(book.getImgUrl())
                .apply(new RequestOptions().error(R.drawable.no_image).placeholder(R.drawable.no_image))
                .into((ImageView) baseViewHolder.getView(R.id.iv_book_img));
        baseViewHolder.setText(R.id.tv_book_item_title_bookname,book.getName());
        baseViewHolder.setText(R.id.tv_book_item_author, book.getAuthor() == null?"作者不详":book.getAuthor());
        String detail;
        if (book.getDesc()!=null && book.getDesc().contains("官术网")){
            detail = "官术网";
        } else {
            detail = "天籁小说网";
        }
        baseViewHolder.setText(R.id.tv_book_item_bookStyle, book.getIsLocal()?"本地书籍":"网络书籍来自:" + detail);
    }

}
