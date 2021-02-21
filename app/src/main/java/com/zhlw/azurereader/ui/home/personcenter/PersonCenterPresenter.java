package com.zhlw.azurereader.ui.home.personcenter;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatDelegate;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.home.MainActivity;
import com.zhlw.azurereader.ui.login.LoginActivity;

public class PersonCenterPresenter implements BasePresenter {

    private PersonCenterFragment mPersonCenterFragment;
    private MainActivity mainActivity;

    PersonCenterPresenter(PersonCenterFragment personCenterFragment){
        mPersonCenterFragment = personCenterFragment;
        //下面是向下转型
        mainActivity = (MainActivity) personCenterFragment.getActivity();//获取view model是为了显示
    }

    @Override
    public void start() {
        mPersonCenterFragment.getmTvLoginName().setText(mainActivity.getViewModel().getmLoginName().getValue());//设置当前登录名
        //里面都是控件的初始化设置
        mPersonCenterFragment.getmShapeTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开登录界面
                Intent intent = new Intent(mPersonCenterFragment.getActivity(), LoginActivity.class);
                mPersonCenterFragment.startActivityForResult(intent,0);
            }
        });

        mPersonCenterFragment.getmTvLoginName().setText(mainActivity.getViewModel().getmLoginName().getValue());//设置当前文字为用户名

        mPersonCenterFragment.getmSwitch().setChecked(!APPCONST.isDayMode);
        mPersonCenterFragment.getmSwitch().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (APPCONST.isDayMode){
                    //没选中时,默认就是日间模式 , 所以点击时要切换为夜间
                    mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    APPCONST.isDayMode = false;
                    mPersonCenterFragment.getmSwitch().setChecked(true);
                } else {
                    //设置app为夜间显示 此时进入这里 isDayMode为 false
                    mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    APPCONST.isDayMode = true;
                    mPersonCenterFragment.getmSwitch().setChecked(false);
                }
                mainActivity.recreate();
            }
        });
    }


}
