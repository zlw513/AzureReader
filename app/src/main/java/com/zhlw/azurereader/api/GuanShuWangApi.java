package com.zhlw.azurereader.api;


import android.util.Log;

import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.utils.GuanShuReadUtil;
import com.zhlw.azurereader.utils.TianLaiReadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 对官术网html文档解析 api
 */
public class GuanShuWangApi extends BaseApi{

    //获取网络章节内容的方法
    public static void getBookChapters(String url,final ResultCallback callback){
        getCommonReturnHtmlStringApi(url, null, "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(GuanShuReadUtil.getChaptersFromHtml((String) o), 0);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 获取章节正文 （网络）这里对不同网站适配以下
     * @param url
     * @param callback
     */
    public static void getChapterContent(String url, final ResultCallback callback){
        int tem = url.indexOf("\"");
        if (tem != -1){
            url = url.substring(0,tem);
        }
        getCommonReturnHtmlStringApi(URLCONST.nameSpace_biyuwu + url, null, "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(GuanShuReadUtil.getContentFormHtml((String)o),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 搜索小说   修改完毕
     * @param key
     * @param callback
     */
    public static void search(String key, final ResultCallback callback){
        Map<String, Object> params = new HashMap<>();
        params.put("q", key);
        getCommonReturnHtmlStringApi(URLCONST.method_biyuwu_search, params, "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                int pageCount = GuanShuReadUtil.getPageNum((String)o);//获取当前搜索结果的总页数  在这里才能获取总页数
                if (pageCount == 0){
                    //如果搜索结果只有一页的时候就只显示这一页嘛
                    callback.onFinish(GuanShuReadUtil.getBooksFromSearchHtml((String)o),code);
                } else {
                    //如果搜索结果不止一页则继续搜索 获取其他分页情况下的数据
                    ArrayList<Book> mbooks = new ArrayList<>(GuanShuReadUtil.getBooksFromSearchHtml((String) o));
                    for (int i=2;i<=pageCount;i++){
                        Map<String, Object> params = new HashMap<>();
                        params.put("q", key);
                        params.put("p", i);
                        getCommonReturnHtmlStringApi(URLCONST.method_biyuwu_search, params, "utf-8", new ResultCallback() {
                            @Override
                            public void onFinish(Object o, int code) {
                                // 这里的返回码都是0
                                mbooks.addAll(GuanShuReadUtil.getBooksFromSearchHtml((String) o));
                                if (mbooks.size() >= (pageCount-1)*10) callback.onFinish(mbooks, code);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

}
