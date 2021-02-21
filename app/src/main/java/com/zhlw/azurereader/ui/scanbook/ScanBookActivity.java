package com.zhlw.azurereader.ui.scanbook;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.ui.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScanBookActivity extends BaseActivity {

    @BindView(R.id.tablayout_scan_book)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager_scan_book)
    ViewPager mViewPager;
    @BindView(R.id.file_system_cb_selected_all)
    CheckBox mCheckBox;
    @BindView(R.id.file_system_btn_delete)
    Button mDeleteButton;
    @BindView(R.id.file_system_btn_add_book)
    Button mAddButton;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);
        unbinder = ButterKnife.bind(this);
        ScanBookPresenter mScanBookPresenter = new ScanBookPresenter(this);
        mScanBookPresenter.start();
    }

    public TabLayout getmTabLayout(){
        return mTabLayout;
    }

    public CheckBox getmCheckBox() {
        return mCheckBox;
    }

    public Button getmDeleteButton() {
        return mDeleteButton;
    }

    public Button getmAddButton() {
        return mAddButton;
    }

    public ViewPager getmViewPager(){
        return mViewPager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
