package com.zhlw.azurereader.ui.classifyinfo;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.BookShopClassifyItemAdapter;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.greendao.service.BookService;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.utils.StringUtils;
import com.zhlw.azurereader.utils.TianLaiReadUtil;
import com.zhlw.azurereader.utils.rxhelper.RxUtils;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

public class ClassifyInfosActivityPresenter implements BasePresenter {

    private ClassifyInfosActivity mActivity;
    private BookService mBookService;

    public ClassifyInfosActivityPresenter(ClassifyInfosActivity classifyInfosActivity) {
        mActivity = classifyInfosActivity;
        mBookService = BookService.getInstance();
    }

    @Override
    public void start() {
        mActivity.getLlTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击退出当前界面
                mActivity.finish();
            }
        });
        String title = mActivity.getIntent().getStringExtra("name");
        mActivity.getTvTitleText().setText(setTitle(title));
        getHtml(title);
    }

    private String setTitle(String title){
        String res = null;
        switch (title){
            case "xuanhuan":
                res = "玄幻小说";
                break;
            case "xiuzhen":
                res = "修真小说";
                break;
            case "dushi":
                res = "都市小说";
                break;
            case "lishi":
                res = "历史小说";
                break;
            case "wangyou":
                res = "网游小说";
                break;
            case "kehuan":
                res = "科幻小说";
                break;
        }
        return res;
    }

    private void getHtml(String classifyName){
        Single.create(new SingleOnSubscribe<ArrayList<Book>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Book>> emitter) throws Exception {
                CommonApi.getbookshopClassify(new ResultCallback() {
                    @Override
                    public void onFinish(Object o, int code) {
                        ArrayList<Book> mBooks = (ArrayList<Book>) o;
                        emitter.onSuccess(mBooks);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }, classifyName);
            }
        }).compose(RxUtils::toSimpleSingle).subscribe(new SingleObserver<ArrayList<Book>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(ArrayList<Book> books) {
                //更新界面
                BookShopClassifyItemAdapter adapter = new BookShopClassifyItemAdapter(R.layout.layout_bookshop_item_readmore,books);
                adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        //将这本书加入书架的操作
                        Book book = books.get(position);
                        Log.d("debuging", "加入的书的书名是"+book.getName());
                        //不在书架，即加入书架
                        mBookService.addBook(book);
                        adapter.notifyDataSetChanged();
                    }
                });
                mActivity.getRvBookshopMore().setLayoutManager(new LinearLayoutManager(mActivity));
                mActivity.getRvBookshopMore().setAdapter(adapter);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

}
