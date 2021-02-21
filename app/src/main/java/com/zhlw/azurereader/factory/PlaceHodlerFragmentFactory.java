package com.zhlw.azurereader.factory;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.zhlw.azurereader.ui.home.localbook.LocalBookFragment;
import com.zhlw.azurereader.ui.home.onlinebook.OnlineBookShopFragment;
import com.zhlw.azurereader.ui.home.personcenter.PersonCenterFragment;

import java.util.ArrayList;
import java.util.List;

public class PlaceHodlerFragmentFactory {
    //简单工厂设计模式
    //负责返回对应情况下的fragment视图
    private static List<Fragment> fragments = new ArrayList<>();

    public static Fragment getCurrentFragment(int index){
        if (fragments.size() == 0){
            Log.d("debuging", "getCurrentFragment: 我创建新的页面了..............");
            fragments.add(LocalBookFragment.newInstance());
            fragments.add(OnlineBookShopFragment.newInstance());
            fragments.add(PersonCenterFragment.newInstacne());
        }
        return fragments.get(index);
    }

}
