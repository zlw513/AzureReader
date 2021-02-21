package com.zhlw.azurereader.api;

import android.util.Log;

import com.zhlw.azurereader.bean.Void;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.URLCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.utils.LocalBookReadContentUtiles;
import com.zhlw.azurereader.utils.TianLaiReadUtil;
import com.zhlw.azurereader.utils.ToastUtils;
import com.zhlw.azurereader.utils.rxhelper.RxUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * 获取书籍内容的类 （重要）
 */
public class CommonApi extends BaseApi{

    /**
     * 获取本地书籍内容的方法 在阅读界面时使用
     * @param localBookReadContentUtiles
     * @param callback
     * @param pos
     */
    public static void getBookChapterContent(LocalBookReadContentUtiles localBookReadContentUtiles,final ResultCallback callback,int pos){
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String result = localBookReadContentUtiles.getContent(pos);
                emitter.onSuccess(result);
            }
        }).compose(RxUtils::toSimpleSingle).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(String result) {
                callback.onFinish(result,0);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    /**
     * 获取本地章节列表
     * @param callback
     */
    public static void getBookChaptersLocal(LocalBookReadContentUtiles localBookReadContentUtiles, final ResultCallback callback){
        Single.create(new SingleOnSubscribe<Void>() {
            @Override
            public void subscribe(SingleEmitter<Void> emitter) throws Exception {
                localBookReadContentUtiles.getChapterList();
                emitter.onSuccess(new Void());
            }
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new SingleObserver<Void>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Void value) {
                        //提示目录加载完成
                        callback.onFinish(localBookReadContentUtiles.converToChapter(), 0);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //数据读取错误
                    }
                });
    }

    /**
     * 获取章节列表（网络）
     * @param url
     * @param callback
     */
    public static void getBookChapters(String url, final ResultCallback callback){
        getCommonReturnHtmlStringApi(url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getChaptersFromHtml((String) o),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * 获取章节正文 （网络）
     * @param url
     * @param callback
     */
    public static void getChapterContent(String url, final ResultCallback callback){
        int tem = url.indexOf("\"");
        if (tem != -1){
            url = url.substring(0,tem);
        }
        getCommonReturnHtmlStringApi(URLCONST.nameSpace_tianlai + url, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getContentFormHtml((String)o),0);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void getbookshopClassify(final ResultCallback callback,String classifyName){
        getCommonReturnHtmlStringApi(URLCONST.nameSpace_tianlai + "/" + classifyName, null, "GBK", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                //返回分类对应的网页
                callback.onFinish(TianLaiReadUtil.getClassifyBooksFromHtml((String) o),code);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 书城爬取网上小说网站内容的方法
     */
    public static void getParsedbookshopContent(final ResultCallback callback,int type){
        // 还是得先获取html中内容，再对它进行解析。它不需要携带参数
        getCommonReturnHtmlStringApi(URLCONST.nameSpace_tianlai, null, "GBK", new ResultCallback() {

            @Override
            public void onFinish(Object o, int code) {
                //这里返回的 o 是字符串，即网页的代码内容
                //然后对这里进行解析 ，获取我们想要的内容，再从callback回调返回。
                if (type == 0) {
                    callback.onFinish(TianLaiReadUtil.getBooksFromHtml((String) o), code);
                } else {
                    callback.onFinish(TianLaiReadUtil.getBooksFromHtmlClassify((String) o), code);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * 搜索小说
     * @param key
     * @param callback
     */
    public static void search(String key, final ResultCallback callback){
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", key);
        getCommonReturnHtmlStringApi(URLCONST.method_buxiu_search, params, "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                int pageCount = TianLaiReadUtil.getPageNum((String)o);//获取当前搜索结果的总页数  在这里才能获取总页数
                if (pageCount == 0){
                    //如果搜索结果只有一页的时候就只显示这一页嘛
                    callback.onFinish(TianLaiReadUtil.getBooksFromSearchHtml((String)o),code);
                } else {
                    //如果搜索结果不止一页则继续搜索 获取其他分页情况下的数据
                    ArrayList<Book> mbooks = new ArrayList<>(TianLaiReadUtil.getBooksFromSearchHtml((String) o));
                    for (int i=2;i<=pageCount;i++){
                        Map<String, Object> params = new HashMap<>();
                        params.put("keyword", key);
                        params.put("page", i);
                        getCommonReturnHtmlStringApi(URLCONST.method_buxiu_search, params, "utf-8", new ResultCallback() {
                            @Override
                            public void onFinish(Object o, int code) {
                                // 这里的返回码都是0
                                mbooks.addAll(TianLaiReadUtil.getBooksFromSearchHtml((String) o));
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

    /**
     * 搜索小说
     * @param bookName
     * @param callback
     */
    public static void searchDeatil(String bookName, final ResultCallback callback){
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", bookName);
        getCommonReturnHtmlStringApi(URLCONST.method_buxiu_search, params, "utf-8", new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                callback.onFinish(TianLaiReadUtil.getBookInfoBySearchHtml((String)o,bookName),code);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public static void getNewestAppVersion(final ResultCallback callback){
        getCommonReturnStringApi(URLCONST.method_getCurAppVersion,null,callback);
    }

}
