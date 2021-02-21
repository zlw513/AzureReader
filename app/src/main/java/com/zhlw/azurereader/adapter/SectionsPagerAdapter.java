package com.zhlw.azurereader.adapter;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zhlw.azurereader.factory.PlaceHodlerFragmentFactory;

import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    List<String> list;//需要显示的fragment在构造器中传入
    public SectionsPagerAdapter(FragmentManager fm, List<String> list) {
        super(fm);
        this.list = list;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return PlaceHodlerFragmentFactory.getCurrentFragment(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}
