package com.zhlw.azurereader.ui.read;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhlw.azurereader.R;
import com.zhlw.azurereader.application.MyApplication;
import com.zhlw.azurereader.ui.BaseActivity;
import com.zhlw.azurereader.viewmodel.MyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadActivity extends BaseActivity {

    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.srl_content)
    SmartRefreshLayout srlContent;
    @BindView(R.id.lv_chapter_list)
    ListView lvChapterList;

    @BindView(R.id.dl_read_activity)
    DrawerLayout dlReadActivity;
    @BindView(R.id.ll_chapter_list_view)
    LinearLayout llChapterListView;
    @BindView(R.id.tv_book_list)
    TextView tvBookList;
    @BindView(R.id.tv_chapter_sort)
    TextView tvChapterSort;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private ReadPresenter mReadPresenter;
    private MyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏应用程序的标题栏，即当前activity的label  继承自AppCompatActivity需要用supportRequestWindowFeature才能生效
        setContentView(R.layout.activity_read);
        ButterKnife.bind(this);
        mReadPresenter = new ReadPresenter(this);
        mReadPresenter.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mReadPresenter.onActivityResult(requestCode, resultCode, data);
    }

    public RecyclerView getRvContent() {
        return rvContent;
    }

    public TextView getTvChapterSort() {
        return tvChapterSort;
    }

    public TextView getTvBookList() {
        return tvBookList;
    }

    public ReadPresenter getmReadPresenter() {
        return mReadPresenter;
    }

    public SmartRefreshLayout getSrlContent() {
        return srlContent;
    }

    public ProgressBar getPbLoading() {
        return pbLoading;
    }

    public MyViewModel getViewModel(){
        if (viewModel == null) viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        return viewModel;
    }

    public ListView getLvChapterList() {
        return lvChapterList;
    }

    public DrawerLayout getDlReadActivity() {
        return dlReadActivity;
    }

    public LinearLayout getLlChapterListView() {
        return llChapterListView;
    }

    @Override
    protected void onDestroy() {
        MyApplication.getApplication().shutdownThreadPool();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
