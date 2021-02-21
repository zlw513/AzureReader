package com.zhlw.azurereader.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<Integer> mProgress;
    private MutableLiveData<Boolean> mSwitchChanged;
    private MutableLiveData<String> mLoginName;//设置登录名

    public MutableLiveData<Integer> getmProgress(){
        if (mProgress == null){
            mProgress = new MutableLiveData<>();
            mProgress.setValue(0);//设置初始值为0
        }
        return mProgress;
    }

    public MutableLiveData<Boolean> getSwitchChanged(){
        if (mSwitchChanged == null){
            mSwitchChanged = new MutableLiveData<>();
            mSwitchChanged.setValue(false);
        }
        return mSwitchChanged;
    }

    public MutableLiveData<String> getmLoginName(){
        if (mLoginName == null){
            mLoginName = new MutableLiveData<>();
            mLoginName.setValue("未登录");
        }
        return mLoginName;
    }

    public void setmProgress(int chapter){
        mProgress.setValue(chapter);
    }

    public void setSwitchChanged(boolean flag){
        mSwitchChanged.setValue(flag);
    }

    public void setLoginName(String name) {
        mLoginName.setValue(name);
    }
}
