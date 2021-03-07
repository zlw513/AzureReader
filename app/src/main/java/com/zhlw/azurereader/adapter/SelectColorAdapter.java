package com.zhlw.azurereader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.custom.SelectColorView;

import java.util.List;

public class SelectColorAdapter extends RecyclerView.Adapter<SelectColorAdapter.SelectColorViewHolder> {

    private List<Integer> colorList;
    private Context mContext;
    private SelectColorOnClickListener mOnClickListener;

    public SelectColorAdapter(List<Integer> colorList, Context context) {
        this.colorList = colorList;
        mContext = context;
    }

    @NonNull
    @Override
    public SelectColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_choose_color,null);
        return new SelectColorViewHolder(view);
    }

    private SelectColorView viewisChecked;

    @Override
    public void onBindViewHolder(@NonNull SelectColorViewHolder holder, int position) {
        holder.selectColorView.setColor(colorList.get(position));
        if (viewisChecked == null && holder.selectColorView.isChecked()) {
            viewisChecked = holder.selectColorView;
        }
        holder.selectColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.selectColorView.isChecked()){
                    //这个被选为true了,此时检查有没有其他是被选中的
                    if (viewisChecked != null){
                        if (viewisChecked.equals(holder.selectColorView)){
                            //是同一个,viewisChecked 保持不变
                        } else {
                            //不是同一个
                            viewisChecked.setChecked(false);
                            viewisChecked = holder.selectColorView;//选中的view换主人啦
                        }
                    } else {
                        viewisChecked = holder.selectColorView;//你是第一个！
                    }
                }
                mOnClickListener.onClick(position,holder.selectColorView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    protected class SelectColorViewHolder extends RecyclerView.ViewHolder{
        private SelectColorView selectColorView;
        public SelectColorViewHolder(@NonNull View itemView) {
            super(itemView);
            selectColorView = itemView.findViewById(R.id.select_color_view);
        }
    }

    public interface SelectColorOnClickListener{
        void onClick(int pos,SelectColorView view);
    }

    public void setmOnClickListener(SelectColorOnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

}
