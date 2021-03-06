package com.zhlw.azurereader.ui.home.personcenter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.zhlw.azurereader.adapter.SelectColorAdapter;
import com.zhlw.azurereader.application.SysManager;
import com.zhlw.azurereader.bean.Setting;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.custom.ColorSelectDialog;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.home.MainActivity;
import com.zhlw.azurereader.ui.login.LoginActivity;
import com.zhlw.azurereader.utils.BrightUtil;

public class PersonCenterPresenter implements BasePresenter {

    private PersonCenterFragment mPersonCenterFragment;
    private MainActivity mainActivity;
    private Setting setting;
    private ColorSelectDialog dialog;

    PersonCenterPresenter(PersonCenterFragment personCenterFragment){
        mPersonCenterFragment = personCenterFragment;
        //下面是向下转型
        mainActivity = (MainActivity) personCenterFragment.getActivity();//获取view model是为了显示
        setting = SysManager.getSetting();
    }

    @Override
    public void start() {
        if (dialog == null){
            dialog = new ColorSelectDialog(mainActivity, Gravity.CENTER, 0,0);
        }
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
                    //没选中时,默认就是日间模式 , 所以点击时要切换为夜
                    BrightUtil.setBrightness(mainActivity, BrightUtil.progressToBright(2));
                    setting.setBrightProgress(2);
                    setting.setBrightFollowSystem(false);
                    SysManager.saveSetting(setting);

                    //mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    APPCONST.isDayMode = false;
                    mPersonCenterFragment.getmSwitch().setChecked(true);
                } else {
                    //设置app为夜间显示 此时进入这里 isDayMode为 false
                    BrightUtil.followSystemBright(mainActivity);
                    setting.setBrightFollowSystem(true);
                    SysManager.saveSetting(setting);

                    //mainActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    APPCONST.isDayMode = true;
                    mPersonCenterFragment.getmSwitch().setChecked(false);
                }
                // mainActivity.recreate(); //会闪屏一下
            }
        });

        mPersonCenterFragment.getmSkinChange().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dialog.isShowing()){
                    dialog.show();
                }
            }
        });


    }

}
