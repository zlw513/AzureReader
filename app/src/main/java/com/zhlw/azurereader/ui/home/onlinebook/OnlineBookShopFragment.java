package com.zhlw.azurereader.ui.home.onlinebook;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.romainpiel.shimmer.ShimmerTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhlw.azurereader.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineBookShopFragment extends Fragment {

    @BindView(R.id.shimmer_tv)
    ShimmerTextView mShimmerTextView;
    @BindView(R.id.srl_bookshop)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.ll_bookshop)
    LinearLayout mLinearLayout;

    @BindView(R.id.tv_book_shop_date)
    TextView mTvDate;//显示每日推荐中  当前日期的
    @BindView(R.id.tv_book_shop_fantisy)
    TextView mTvFantisy;
    @BindView(R.id.tv_book_shop_kongfu)
    TextView mTvKongfu;
    @BindView(R.id.tv_book_shop_science)
    TextView mTvScience;
    @BindView(R.id.tv_book_shop_city)
    TextView mTvCity;
    @BindView(R.id.tv_book_shop_history)
    TextView mTvHistory;
    @BindView(R.id.tv_book_shop_gamenovel)
    TextView mTvGame;

    @BindView(R.id.rv_daily_recomment)
    RecyclerView mRvDailyRecomment;
    @BindView(R.id.rv_fantisy_noval)
    RecyclerView mRvFantisy;
    @BindView(R.id.rv_kongfu_noval)
    RecyclerView mRvKongfu;
    @BindView(R.id.rv_science_noval)
    RecyclerView mRvScience;
    @BindView(R.id.rv_city_noval)
    RecyclerView mRvCity;
    @BindView(R.id.rv_history_noval)
    RecyclerView mRvHistory;
    @BindView(R.id.rv_game_noval)
    RecyclerView mRvGameNovel;

    @BindView(R.id.sv_online_bookshop)
    ScrollView mScrollView;

    private Unbinder binder;

    private OnlineBookShopFragmentPresenter mPresenter;

    public OnlineBookShopFragment() {
        // Required empty public constructor
    }

    public static OnlineBookShopFragment newInstance(){
        OnlineBookShopFragment fragment = new OnlineBookShopFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_online_book_shop, container, false);
        binder = ButterKnife.bind(this,v);
        mPresenter = new OnlineBookShopFragmentPresenter(this);
        mPresenter.start();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getData();
    }

    public ShimmerTextView getmShimmerTextView() {
        return mShimmerTextView;
    }

    public TextView getmTvDate() {
        return mTvDate;
    }

    public TextView getmTvFantisy() {
        return mTvFantisy;
    }

    public TextView getmTvKongfu() {
        return mTvKongfu;
    }

    public TextView getmTvScience() {
        return mTvScience;
    }

    public TextView getmTvCity() {
        return mTvCity;
    }

    public TextView getmTvHistory() {
        return mTvHistory;
    }

    public TextView getmTvGame() {
        return mTvGame;
    }

    public RecyclerView getmRvDailyRecomment() {
        return mRvDailyRecomment;
    }

    public RecyclerView getmRvFantisy() {
        return mRvFantisy;
    }

    public RecyclerView getmRvKongfu() {
        return mRvKongfu;
    }

    public RecyclerView getmRvScience() {
        return mRvScience;
    }

    public RecyclerView getmRvCity() {
        return mRvCity;
    }

    public RecyclerView getmRvHistory() {
        return mRvHistory;
    }

    public RecyclerView getmRvGameNovel() {
        return mRvGameNovel;
    }

    public SmartRefreshLayout getmSmartRefreshLayout() {
        return mSmartRefreshLayout;
    }

    public LinearLayout getmLinearLayout() {
        return mLinearLayout;
    }

    public ScrollView getmScrollView() {
        return mScrollView;
    }
}
