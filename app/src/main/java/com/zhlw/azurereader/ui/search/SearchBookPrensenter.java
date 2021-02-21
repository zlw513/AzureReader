package com.zhlw.azurereader.ui.search;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.zhlw.azurereader.R;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.api.GuanShuWangApi;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.greendao.entity.SearchHistory;
import com.zhlw.azurereader.greendao.service.SearchHistoryService;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.bookinfo.BookInfoActivity;
import com.zhlw.azurereader.utils.StringHelper;
import com.zhlw.azurereader.utils.ToastUtils;

import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;


public class SearchBookPrensenter implements BasePresenter {

    private SearchBookActivity mSearchBookActivity;
    private SearchBookAdapter mSearchBookAdapter;
    private String searchKey;//搜索关键字
    private ArrayList<Book> mBooks = new ArrayList<>();
    private ArrayList<SearchHistory> mSearchHistories = new ArrayList<>();
    private ArrayList<String> mSuggestions = new ArrayList<>();

    private SearchHistoryService mSearchHistoryService;

    private SearchHistoryAdapter mSearchHistoryAdapter;

    private int inputConfirm = 0;//搜索输入确认
    private int confirmTime = 1000;//搜索输入确认时间（毫秒）

    //下面是默认的一些推荐
    private static String[] suggestion = {"不朽凡人", "圣墟", "我是至尊" ,"龙王传说", "太古神王", "一念永恒", "雪鹰领主", "大主宰"};

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    search();
                    break;
                case 2:
                    initSearchList();
                    break;
                case 3:
                    //搜不到结果
                    mSearchBookActivity.getLvSearchBooksList().setAdapter(null);
                    mSearchBookActivity.getPbLoading().setVisibility(View.GONE);
                    break;
            }
        }
    };

    public SearchBookPrensenter(SearchBookActivity searchBookActivity) {
        mSearchBookActivity = searchBookActivity;
        mSearchHistoryService = new SearchHistoryService();
        for (int i = 0; i < suggestion.length; i++) {
            mSuggestions.add(suggestion[i]);
        }
    }

    @Override
    public void start() {
        mSearchBookActivity.getTvTitleText().setText("搜索");
        mSearchBookActivity.getLlTitleBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchBookActivity.finish();
            }
        });
        mSearchBookActivity.getEtSearchKey().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                searchKey = editable.toString();//这里获得搜索关键词
                if (StringHelper.isEmpty(searchKey)) {
                    search();
                }
            }
        });

        //在相关书籍的点击事件中发数据到bookinfo activity  这个是展示搜索结果的listview
        mSearchBookActivity.getLvSearchBooksList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mSearchBookActivity, BookInfoActivity.class);//这里往bookinfo activity里发数据，所以在那边可以收到
                intent.putExtra(APPCONST.BOOK, mBooks.get(i));
                mSearchBookActivity.startActivity(intent);
            }
        });
        mSearchBookActivity.getTvSearchSource().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("天籁".equals(mSearchBookActivity.getTvSearchSource().getText().toString())){
                    mSearchBookActivity.getTvSearchSource().setText("官术");
                } else {
                    mSearchBookActivity.getTvSearchSource().setText("天籁");
                }
            }
        });
        mSearchBookActivity.getTvSearchConform().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        mSearchBookActivity.getTgSuggestBook().setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                mSearchBookActivity.getEtSearchKey().setText(tag);
                search();
            }
        });
        mSearchBookActivity.getLvHistoryList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchBookActivity.getEtSearchKey().setText(mSearchHistories.get(position).getContent());
                search();
            }
        });
        mSearchBookActivity.getLlClearHistory().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchHistoryService.clearHistory();
                initHistoryList();
            }
        });
        initSuggestionBook();
        initHistoryList();
    }

    /**
     * 初始化建议书目
     */
    private void initSuggestionBook() {
        mSearchBookActivity.getTgSuggestBook().setTags(suggestion);
    }

    /**
     * 初始化历史列表
     */
    private void initHistoryList() {
        mSearchHistories = mSearchHistoryService.findAllSearchHistory();
        if (mSearchHistories == null || mSearchHistories.size() == 0) {
            mSearchBookActivity.getLlHistoryView().setVisibility(View.GONE);
        } else {
            mSearchHistoryAdapter = new SearchHistoryAdapter(mSearchBookActivity, R.layout.listview_search_history_item, mSearchHistories);
            mSearchBookActivity.getLvHistoryList().setAdapter(mSearchHistoryAdapter);
            mSearchBookActivity.getLlHistoryView().setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化搜索列表
     */
    private void initSearchList() {
        mSearchBookAdapter = new SearchBookAdapter(mSearchBookActivity, R.layout.listview_search_book_item, mBooks);
        mSearchBookActivity.getLvSearchBooksList().setAdapter(mSearchBookAdapter);
        mSearchBookActivity.getLvSearchBooksList().setVisibility(View.VISIBLE);
        mSearchBookActivity.getLlSuggestBooksView().setVisibility(View.GONE);
        mSearchBookActivity.getLlHistoryView().setVisibility(View.GONE);
        mSearchBookActivity.getPbLoading().setVisibility(View.GONE);
    }

    /**
     * 获取搜索数据 (这里分两种情况好了)
     */
    private void getData() {
        mBooks.clear();//mbooks是返回的结果列表
        if ("天籁".equals(mSearchBookActivity.getTvSearchSource().getText().toString())){
            Log.d("debuging", "使用天籁网搜索 ");
            CommonApi.search(searchKey, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    mBooks = (ArrayList<Book>) o;
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }

                @Override
                public void onError(Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(3));
                }
            });
        } else {
            Log.d("debuging", "使用官术网搜索 ");
            GuanShuWangApi.search(searchKey, new ResultCallback() {
                @Override
                public void onFinish(Object o, int code) {
                    mBooks = (ArrayList<Book>) o;
                    mHandler.sendMessage(mHandler.obtainMessage(2));
                }

                @Override
                public void onError(Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(3));
                }
            });
        }
    }

    /**
     * 搜索
     */
    private void search() {
        mSearchBookActivity.getPbLoading().setVisibility(View.VISIBLE);
        if (StringHelper.isEmpty(searchKey)) {
            mSearchBookActivity.getPbLoading().setVisibility(View.GONE);
            mSearchBookActivity.getLvSearchBooksList().setVisibility(View.GONE);
            mSearchBookActivity.getLlSuggestBooksView().setVisibility(View.VISIBLE);
            initHistoryList();
            mSearchBookActivity.getLvSearchBooksList().setAdapter(null);
        } else {
            //简单来说就是有搜索关键词就执行搜索功能
            mSearchBookActivity.getLvSearchBooksList().setVisibility(View.VISIBLE);
            mSearchBookActivity.getLlSuggestBooksView().setVisibility(View.GONE);
            mSearchBookActivity.getLlHistoryView().setVisibility(View.GONE);
            getData();
            mSearchHistoryService.addOrUpadteHistory(searchKey);
        }
    }

    public boolean onBackPressed() {
        if (StringHelper.isEmpty(searchKey)) {
            return false;
        } else {
            mSearchBookActivity.getEtSearchKey().setText("");
            return true;
        }
    }

}

