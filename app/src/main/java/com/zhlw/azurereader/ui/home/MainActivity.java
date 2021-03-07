package com.zhlw.azurereader.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.SectionsPagerAdapter;
import com.zhlw.azurereader.custom.CustomTabLayout;
import com.zhlw.azurereader.ui.BaseActivity;
import com.zhlw.azurereader.ui.search.SearchBookActivity;
import com.zhlw.azurereader.utils.ThemeUtils;
import com.zhlw.azurereader.viewmodel.MyViewModel;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements ThemeUtils.OnThemeChangeListener {

    private static final String[] TAB_TITLES = new String[]{"本地书架","网上书城","个人中心"};
    private List<String> titles = Arrays.asList(TAB_TITLES);
    private ViewPager viewPager;
    private CustomTabLayout tabLayout;
    private TextView toolBarTitle;
    private Switch aSwitch;
    private TextView gridModel;
    private MyViewModel viewModel;
    private AppBarLayout appbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeUtils.registerThemeChangeListener(this);
        bindView();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTheme(getApplicationContext());
    }


    private void bindView(){
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolBarTitle = findViewById(R.id.tv_toolbar_title);
        aSwitch = findViewById(R.id.switch_button);
        gridModel = findViewById(R.id.tv_switch_gongge);
        appbar_layout = findViewById(R.id.appbar_layout);
    }

    private void init(){
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),titles);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setTitle(titles);
        tabLayout.setTabRippleColor(ColorStateList.valueOf(getColor(R.color.transprent)));//取消点击时透明的颜色
        toolBarTitle.setOnTouchListener((v, event) -> {
            //左上右下分别对应 0  1  2  3
            Drawable drawable = toolBarTitle.getCompoundDrawables()[2];//2就代表 右侧的drawable图片
            if (drawable == null){
                return false;
            }
            if (event.getX() > toolBarTitle.getWidth() - drawable.getBounds().width()){
                Intent intent = new Intent(MainActivity.this, SearchBookActivity.class);
                startActivity(intent);
                return false;
            }
            return false;
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int postion = tab.getPosition();
                if (postion == 0) {
                    aSwitch.setClickable(true);
                    aSwitch.setThumbResource(R.drawable.switch_custom_thumb_on);
                } else {
                    aSwitch.setClickable(false);
                    aSwitch.setThumbResource(R.drawable.switch_custom_thumb_unuseable);
                }
                toolBarTitle.setText(titles.get(postion));
                viewPager.setCurrentItem(postion);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SharedPreferences preferences = getSharedPreferences("switchflag", MODE_PRIVATE);
        if (preferences.getBoolean("switchflag",false)){
            aSwitch.setChecked(true);
            aSwitch.setTextColor(getResources().getColor(R.color.colorWrite,null));
            gridModel.setTextColor(getResources().getColor(R.color.sys_red2,null));
        }
        //这个方法只是在启动时才会调用
    }

    public ViewPager getViewPager(){
        return viewPager;
    }

    public Switch getaSwitch(){
        return aSwitch;
    }

    public TextView getGridModel(){
        return gridModel;
    }

    public MyViewModel getViewModel(){
        if (viewModel == null) viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        return viewModel;
    }

    @Override
    protected void onStop() {
        Log.d("debuging", "onStop: running......................");
        SharedPreferences.Editor editor = getSharedPreferences("switchflag", Context.MODE_PRIVATE).edit();
        editor.putBoolean("switchflag",aSwitch.isChecked());
        editor.apply();//我这里需要立刻修正数据
        super.onStop();
    }

    @Override
    public void onThemeChanged() {
        //重新设置状态栏颜色
        initTheme(getApplicationContext());
    }

    public void initTheme(Context context) {
        appbar_layout.setBackgroundColor(ThemeUtils.getmThereColor(context));
        viewPager.setBackgroundColor(ThemeUtils.getmThereColor(context));
        // 设置状态栏颜色 api21以上的方法
        Window window = getWindow();
        window.setStatusBarColor(ThemeUtils.getmThereColor(context));
    }

}
