package com.zhlw.azurereader.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zhlw.azurereader.R;

import java.util.List;

public class CustomTabLayout extends TabLayout {

    private List<String> mTitles;

    public CustomTabLayout(Context context) {
        super(context);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                /*
                 * 设置当前选中的Tab为特殊高亮样式。
                 */
                if (tab != null && tab.getCustomView() != null){
                    TextView title = tab.getCustomView().findViewById(R.id.tab_text);
                    TextPaint paint = title.getPaint();
                    paint.setFakeBoldText(true);//设置text view的字体加粗

                    title.setTextColor(Color.WHITE);
                    title.setBackgroundResource(R.drawable.tablayout_item_pressed);

                    ImageView indicator = tab.getCustomView().findViewById(R.id.tab_indicator);
                    indicator.setBackgroundResource(R.drawable.tablayout_item_indicator);
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {
                /*
                 * 重置所有未选中的Tab颜色、字体、背景恢复常态(未选中状态)。
                 */
                if (tab != null && tab.getCustomView() != null) {
                    TextView textview = tab.getCustomView().findViewById(R.id.tab_text);

                    textview.setTextColor(getResources().getColor(android.R.color.white));
                    TextPaint paint = textview.getPaint();
                    paint.setFakeBoldText(false);
                    textview.setBackgroundResource(R.drawable.tablayout_item_normal);

                    ImageView tab_indicator = tab.getCustomView().findViewById(R.id.tab_indicator);
                    tab_indicator.setBackgroundResource(0);//使用0做参数时代表移除background
                }
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    public void setTitle(List<String> titles) {
        this.mTitles = titles;
        /**
         * 开始添加切换的Tab。
         */
        for (String title : this.mTitles) {
            Tab tab = newTab();//注意这里是newtab 连起来的！
            tab.setCustomView(R.layout.tablayout_item);
            if (tab.getCustomView() != null) {
                TextView tabtitle = tab.getCustomView().findViewById(R.id.tab_text);
                tabtitle.setText(title);
            }
            this.addTab(tab);
        }
    }

}
