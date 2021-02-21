package com.zhlw.azurereader.callback;


import com.zhlw.azurereader.bean.JsonModel;

public interface JsonCallback {

    void onFinish(JsonModel jsonModel);

    void onError(Exception e);

}
