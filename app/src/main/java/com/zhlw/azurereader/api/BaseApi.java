package com.zhlw.azurereader.api;

import com.google.gson.Gson;
import com.zhlw.azurereader.application.SysManager;
import com.zhlw.azurereader.bean.JsonModel;
import com.zhlw.azurereader.callback.JsonCallback;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.ErrorCode;
import com.zhlw.azurereader.source.HttpDataSource;
import com.zhlw.azurereader.utils.HttpUtil;
import com.zhlw.azurereader.utils.JsonArrayToObjectArray;
import com.zhlw.azurereader.utils.TextHelper;

import java.util.Map;

public class BaseApi {

    /**
     * post通用返回实体api
     * @param url
     * @param params
     * @param c 返回的实体类型
     * @param callback
     */
    protected static void postCommonApi(String url, Map<String, Object> params, final Class c, final ResultCallback callback) {
        HttpDataSource.httpPost(url, HttpUtil.makePostOutput(params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    callback.onFinish(new Gson().fromJson(jsonModel.getResult(),c), jsonModel.getError());
                } else {
                    noSuccess(jsonModel,callback);
                }
            }
            @Override
            public void onError(Exception e) {
                error(e,callback);
            }
        });
    }

    /**
     * post通用返回字符串api
     * @param url
     * @param params
     * @param callback
     */
    protected static void postCommonReturnStringApi(String url, Map<String, Object> params, final ResultCallback callback) {
        HttpDataSource.httpPost(url, HttpUtil.makePostOutput(params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    callback.onFinish(jsonModel.getResult(), jsonModel.getError());
                }else {
                    noSuccess(jsonModel,callback);
                }
            }
            @Override
            public void onError(Exception e) {
                error(e,callback);
            }
        });
    }

    /**
     * get通用返回实体api
     * @param url
     * @param params
     * @param c 返回的实体类型
     * @param callback
     */
    protected static void getCommonApi(String url, Map<String, Object> params, final Class c, final ResultCallback callback) {
        HttpDataSource.httpGet(HttpUtil.makeURL(url,params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    callback.onFinish(new Gson().fromJson(jsonModel.getResult(),c), jsonModel.getError());
                } else {
                    noSuccess(jsonModel,callback);
                }
            }

            @Override
            public void onError(Exception e) {
               error(e,callback);
            }
        });
    }

    /**
     * get通用返回字符串api
     * @param url
     * @param params
     * @param callback
     */
    protected static void getCommonReturnStringApi(String url, Map<String, Object> params, final ResultCallback callback) {
        HttpDataSource.httpGet(HttpUtil.makeURL(url,params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    callback.onFinish(jsonModel.getResult(), jsonModel.getError());
                } else {
                    noSuccess(jsonModel,callback);
                }
            }
            @Override
            public void onError(Exception e) {
              error(e,callback);
            }
        });
    }

    /**
     * get通用返回html字符串api  用于解析网页html
     * @param url
     * @param params
     * @param callback
     */
    protected static void getCommonReturnHtmlStringApi(String url, Map<String, Object> params, String charsetName, final ResultCallback callback) {
        HttpDataSource.httpGet_html(HttpUtil.makeURL(url, params), charsetName, new ResultCallback() {

            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(o,code);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
//                error(e,callback);
            }
        });
    }

    /**
     * get通用获取实体列表api
     * @param url
     * @param params
     * @param c 返回是列表实体类型
     * @param callback
     */
    protected static void getCommonListApi(String url, Map<String, Object> params, final Class c, final ResultCallback callback) {
        HttpDataSource.httpGet(HttpUtil.makeURL(url,params), new JsonCallback() {
            @Override
            public void onFinish(JsonModel jsonModel) {
                if (jsonModel.isSuccess()) {
                    try {
                        callback.onFinish(JsonArrayToObjectArray.getArray(jsonModel.getResult(),c), jsonModel.getError());
                    }catch (Exception e){
                        callback.onError(e);
                        e.printStackTrace();
                    }
                } else {
                    noSuccess(jsonModel,callback);
                }
            }

            @Override
            public void onError(Exception e) {
               error(e,callback);
            }
        });
    }

    /**
     * api异常处理
     * @param e
     * @param callback
     */
    private static void error(Exception e, final ResultCallback callback){
      /*  if (e.toString().contains("SocketTimeoutException") || e.toString().contains("UnknownHostException")) {
            TextHelper.showText("网络连接超时，请检查网络");
        }*/
        e.printStackTrace();
        callback.onError(e);
    }

    /**
     * api请求失败处理
     * @param jsonModel
     * @param callback
     */
    private static void noSuccess(JsonModel jsonModel, ResultCallback callback){
        if (!jsonModel.isSuccess()) {
            if (jsonModel.getError() == ErrorCode.no_security) {
                TextHelper.showText("登录过期，请重新登录");
                SysManager.logout();
            } else {
                if (jsonModel.getError() == 0) {
                    callback.onFinish(jsonModel.getResult(), -1);
                } else {
                    callback.onFinish(jsonModel.getResult(), jsonModel.getError());
                }
            }
        }
    }
}
