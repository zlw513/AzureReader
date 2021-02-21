package com.zhlw.azurereader.ui.search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.utils.StringHelper;

import java.util.ArrayList;

public class SearchBookAdapter extends ArrayAdapter<Book> {

    private int mResourceId;

    public SearchBookAdapter(Context context, int resourceId, ArrayList<Book> datas){
        super(context,resourceId,datas);
        mResourceId = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResourceId,null);
            viewHolder.ivBookImg = (ImageView) convertView.findViewById(R.id.iv_book_img);
            viewHolder.tvBookName = (TextView) convertView.findViewById(R.id.tv_book_name);
            viewHolder.tvAuthor = (TextView) convertView.findViewById(R.id.tv_book_author);
            viewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tv_book_desc);
            viewHolder.tvType = (TextView) convertView.findViewById(R.id.tv_book_type);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        initView(position,viewHolder);
        return convertView;
    }

    private void initView(int postion,ViewHolder viewHolder){
        Book book = getItem(postion);
        if (StringHelper.isEmpty(book.getImgUrl())){
            book.setImgUrl("");
        }
        Log.d("debug", "initView: "+book.getName());
        Glide.with(getContext())
                .load(book.getImgUrl())
                .apply(new RequestOptions().error(R.drawable.no_image).placeholder(R.drawable.no_image))
                .into(viewHolder.ivBookImg);
        viewHolder.tvBookName.setText(book.getName());
        viewHolder.tvDesc.setText(book.getDesc());
        viewHolder.tvAuthor.setText(book.getAuthor());
        viewHolder.tvType.setText(book.getType());
    }

    class ViewHolder{
        ImageView ivBookImg;
        TextView tvBookName;
        TextView tvDesc;
        TextView tvAuthor;
        TextView tvType;
    }

}
