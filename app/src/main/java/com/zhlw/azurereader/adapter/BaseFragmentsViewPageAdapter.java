package com.zhlw.azurereader.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by Liang_Lu on 2017/11/29.
 */
public class BaseFragmentsViewPageAdapter extends FragmentPagerAdapter {
    private String[] titleArray;
    private List<Fragment> fragments;

    public BaseFragmentsViewPageAdapter(FragmentManager fm, String[] titleArray, List<Fragment> fragments) {
        super(fm);
        this.titleArray = titleArray;
        this.fragments=fragments;
    }

    //为tablayout设置title的方法
    @Override
    public CharSequence getPageTitle(int position) {
        return titleArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return titleArray.length;
    }

}
