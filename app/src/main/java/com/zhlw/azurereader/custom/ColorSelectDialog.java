package com.zhlw.azurereader.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.SelectColorAdapter;
import com.zhlw.azurereader.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class ColorSelectDialog extends Dialog implements SelectColorAdapter.SelectColorOnClickListener {

    private Context mContext;
    private RecyclerView recyclerView;
    private List<Integer> colorList;
    private int mGravity = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    public ColorSelectDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public ColorSelectDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected ColorSelectDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public ColorSelectDialog(Context context, int gravity, int width, int height){
        super(context, R.style.Dialog);
        this.mContext = context;
        this.mGravity = gravity;
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_color_dialog);
        initColor();
        initview();
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = mGravity !=0 ? mGravity : Gravity.CENTER;
        layoutParams.width = mWidth != 0 ? mWidth : WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = mHeight != 0 ? mHeight : WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }

    private void initColor() {
        colorList = new ArrayList<>();
        colorList.add(Color.parseColor("#a1c4fd"));
        colorList.add(Color.parseColor("#d4fc79"));
        colorList.add(Color.parseColor("#f093fb"));
        colorList.add(Color.parseColor("#ebedee"));
        colorList.add(Color.parseColor("#fee140"));
        colorList.add(Color.parseColor("#30cfd0"));
        colorList.add(Color.parseColor("#667eea"));
        colorList.add(Color.parseColor("#3cba92"));
        colorList.add(Color.parseColor("#c79081"));
        colorList.add(Color.parseColor("#008577"));
    }

    SelectColorAdapter adapter;

    private void initview() {
        recyclerView = findViewById(R.id.rv_select_color);
        adapter = new SelectColorAdapter(colorList,mContext);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4, RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setmOnClickListener(this);
    }

    @Override
    public void onClick(int pos, SelectColorView view) {
        Log.d("zlww", "=====onClick:====== "+view.isChecked());
        if (!view.isChecked()){
            //证明当前是选中了,那么就改变主题颜色吧
            ThemeUtils.setThereColor(colorList.get(pos));
        }
        view.setChecked(!view.isChecked());
    }

}
