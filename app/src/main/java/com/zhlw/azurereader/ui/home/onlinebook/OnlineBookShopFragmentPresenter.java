package com.zhlw.azurereader.ui.home.onlinebook;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.romainpiel.shimmer.Shimmer;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.adapter.BookShopItemAdapter;
import com.zhlw.azurereader.api.CommonApi;
import com.zhlw.azurereader.callback.ResultCallback;
import com.zhlw.azurereader.constant.APPCONST;
import com.zhlw.azurereader.greendao.entity.Book;
import com.zhlw.azurereader.presenter.BasePresenter;
import com.zhlw.azurereader.ui.bookinfo.BookInfoActivity;
import com.zhlw.azurereader.ui.classifyinfo.ClassifyInfosActivity;
import com.zhlw.azurereader.utils.DateHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlineBookShopFragmentPresenter implements BasePresenter {

    private OnlineBookShopFragment mFragment;
    private BookShopItemAdapter mBookShopItemAdapter; //这里是推荐的
    private boolean initFlag = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initBook((ArrayList<Book>) msg.obj);
                    break;
                case 2:
                    initBook((HashMap<String,ArrayList<Book>>) msg.obj);
                    break;
            }
        }
    };

    OnlineBookShopFragmentPresenter(OnlineBookShopFragment onlineBookShopFragment){
        mFragment = onlineBookShopFragment;
    }

    @Override
    public void start() {
        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(1600).setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start(mFragment.getmShimmerTextView());

        mFragment.getmSmartRefreshLayout().autoRefreshAnimationOnly();//开启加载动画

        mFragment.getmTvDate().setText(DateHelper.longToTime3(DateHelper.getLongDate()));//设置今天的日期的

        mFragment.getmTvFantisy().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "xuanhuan");
                mFragment.getContext().startActivity(intent);
            }
        });

        mFragment.getmTvKongfu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "xiuzhen");
                mFragment.getContext().startActivity(intent);
            }
        });

        mFragment.getmTvScience().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "kehuan");
                mFragment.getContext().startActivity(intent);
            }
        });

        mFragment.getmTvCity().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "dushi");
                mFragment.getContext().startActivity(intent);
            }
        });

        mFragment.getmTvHistory().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "lishi");
                mFragment.getContext().startActivity(intent);
            }
        });

        mFragment.getmTvGame().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mFragment.getContext(), ClassifyInfosActivity.class);
                intent.putExtra("name", "wangyou");
                mFragment.getContext().startActivity(intent);
            }
        });

    }

    //此方法是获取数据的方法，
    public void getData(){

        mFragment.getmScrollView().scrollTo(0, 0);
        //发送网络请求，调用获取数据的方法

        //获取推荐的方法
        CommonApi.getParsedbookshopContent(new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                //完成后可以封装书籍进adapter里面了
                ArrayList<Book> mBooks = (ArrayList<Book>) o;
                handler.sendMessage(handler.obtainMessage(1,mBooks));//通知刷新
            }

            @Override
            public void onError(Exception e) {

            }
        },0);

        //获取其他书籍分类的方法
        CommonApi.getParsedbookshopContent(new ResultCallback() {
            @Override
            public void onFinish(Object o, int code) {
                //完成后可以封装书籍进adapter里面了
                HashMap<String,ArrayList<Book>> mBooks = (HashMap<String,ArrayList<Book>>) o;
                handler.sendMessage(handler.obtainMessage(2,mBooks));//通知刷新
            }

            @Override
            public void onError(Exception e) {

            }
        },1);
    }

    //分类的adapter
    private void initBook(HashMap<String,ArrayList<Book>> mBooks){
        if (mBooks != null && initFlag){
            initFlag = false;
            BookShopItemAdapter bookShopItemAdapterFantisy = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("玄幻"));
            BookShopItemAdapter bookShopItemAdapterKongFu = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("修真"));
            BookShopItemAdapter bookShopItemAdapterCity = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("都市"));
            BookShopItemAdapter bookShopItemAdapterHistory = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("历史"));
            BookShopItemAdapter bookShopItemAdapterGame = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("网游"));
            BookShopItemAdapter bookShopItemAdapterScience = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks.get("科幻"));

            //=================================为每个分类设置adapter======================================//
            bookShopItemAdapterFantisy.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("玄幻").get(position));
                    mFragment.startActivity(intent);
                }
            });

            bookShopItemAdapterKongFu.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("修真").get(position));
                    mFragment.startActivity(intent);
                }
            });

            bookShopItemAdapterCity.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("都市").get(position));
                    mFragment.startActivity(intent);
                }
            });

            bookShopItemAdapterHistory.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("历史").get(position));
                    mFragment.startActivity(intent);
                }
            });

            bookShopItemAdapterGame.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("网游").get(position));
                    mFragment.startActivity(intent);
                }
            });

            bookShopItemAdapterScience.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                    intent.putExtra(APPCONST.BOOK, mBooks.get("科幻").get(position));
                    mFragment.startActivity(intent);
                }
            });

            //==========================================================================================//
            mFragment.getmRvFantisy().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvFantisy().setAdapter(bookShopItemAdapterFantisy);

            mFragment.getmRvKongfu().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvKongfu().setAdapter(bookShopItemAdapterKongFu);

            mFragment.getmRvCity().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvCity().setAdapter(bookShopItemAdapterCity);

            mFragment.getmRvHistory().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvHistory().setAdapter(bookShopItemAdapterHistory);

            mFragment.getmRvGameNovel().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvGameNovel().setAdapter(bookShopItemAdapterGame);

            mFragment.getmRvScience().setLayoutManager(new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false));
            mFragment.getmRvScience().setAdapter(bookShopItemAdapterScience);
        }
        mFragment.getmLinearLayout().setVisibility(View.VISIBLE);
        mFragment.getmSmartRefreshLayout().finishRefresh();
    }

    private void initBook(ArrayList<Book> mBooks){
        if (mBooks != null){
            if (mBookShopItemAdapter == null){
                mBookShopItemAdapter = new BookShopItemAdapter(R.layout.gridview_book_item, mBooks);
                mBookShopItemAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        //跳到相应书籍的详情页面
                        Intent intent = new Intent(mFragment.getContext(), BookInfoActivity.class);
                        intent.putExtra(APPCONST.BOOK, mBooks.get(position));
                        mFragment.startActivity(intent);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mFragment.getContext(), RecyclerView.HORIZONTAL,false);
                mFragment.getmRvDailyRecomment().setLayoutManager(linearLayoutManager);
                mFragment.getmRvDailyRecomment().setAdapter(mBookShopItemAdapter);
            } else {
                mBookShopItemAdapter.notifyDataSetChanged();
            }
        }
    }

}
